package com.example.gametrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gametrack.GameViewModel
import com.example.gametrack.data.Game
import com.example.gametrack.ui.theme.*
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: GameViewModel) {
    val games by viewModel.games.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var gameToDelete by remember { mutableStateOf<Game?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var expandedFilter by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Por defecto") }

    LaunchedEffect(currentUser) {
        if (currentUser?.email != null) {
            viewModel.loadGamesDefault()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("🗑️ Eliminar Juego", color = HomePrimary) },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar \"${gameToDelete?.nombre}\"?",
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        gameToDelete?.let { viewModel.deleteGame(it) }
                        showDeleteDialog = false
                        gameToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HomeAccent,
                        contentColor = DarkBackground
                    )
                ) {
                    Text("✅ Sí, eliminar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        gameToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = HomeSecondary
                    )
                ) {
                    Text("❌ Cancelar")
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("🚪 Cerrar Sesión", color = HomePrimary) },
            text = {
                Text("¿Estás seguro de que quieres cerrar sesión?", color = TextPrimary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HomeAccent,
                        contentColor = DarkBackground
                    )
                ) {
                    Text("✅ Sí, salir")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = HomeSecondary
                    )
                ) {
                    Text("❌ Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "🎮 GameTrack",
                        style = MaterialTheme.typography.titleLarge,
                        color = HomePrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = HomePrimary
                ),
                actions = {
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = HomeSecondary
                            )
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("👤 Mi Perfil", color = HomePrimary) },
                                onClick = {
                                    expanded = false
                                    navController.navigate("profile")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🚪 Cerrar Sesión", color = HomePrimary) },
                                onClick = {
                                    expanded = false
                                    showLogoutDialog = true
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add") },
                containerColor = HomeButton,
                contentColor = DarkBackground
            ) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            // Header con filtro
            Box(Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 16.dp)) {
                Text(
                    text = "Tu Biblioteca",
                    style = MaterialTheme.typography.titleMedium,
                    color = HomePrimary,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                Box(Modifier.align(Alignment.CenterEnd)) {
                    Button(
                        onClick = { expandedFilter = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = HomeSecondary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sort, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Ordenar")
                        }
                    }

                    DropdownMenu(expanded = expandedFilter, onDismissRequest = { expandedFilter = false }) {
                        DropdownMenuItem(
                            text = { Text("📊 Ordenar por:", color = HomePrimary) },
                            onClick = {}
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("🔄 Por defecto", color = HomeSecondary) },
                            onClick = {
                                selectedFilter = "Por defecto"
                                viewModel.loadGamesDefault()
                                expandedFilter = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🔤 Nombre A-Z", color = HomeSecondary) },
                            onClick = {
                                selectedFilter = "Nombre A-Z"
                                viewModel.loadGamesOrderedByNameAsc()
                                expandedFilter = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🔤 Nombre Z-A", color = HomeSecondary) },
                            onClick = {
                                selectedFilter = "Nombre Z-A"
                                viewModel.loadGamesOrderedByNameDesc()
                                expandedFilter = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("⏱️ Más horas", color = HomeAccent) },
                            onClick = {
                                selectedFilter = "Más horas"
                                viewModel.loadGamesOrderedByHoursDesc()
                                expandedFilter = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("⭐ Mejor calificación", color = HomeAccent) },
                            onClick = {
                                selectedFilter = "Mejor calificación"
                                viewModel.loadGamesOrderedByRatingDesc()
                                expandedFilter = false
                            }
                        )
                    }
                }
            }

            if (games.isEmpty()) {
                EmptyState()
            } else {
                GamesList(
                    games = games,
                    onDeleteClick = { game ->
                        gameToDelete = game
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🎮", style = MaterialTheme.typography.headlineLarge, color = HomePrimary)
            Text(
                "Tu biblioteca está vacía",
                style = MaterialTheme.typography.titleMedium,
                color = HomePrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Comienza agregando tu primer juego con el botón +",
                style = MaterialTheme.typography.bodyMedium,
                color = HomeSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GamesList(games: List<Game>, onDeleteClick: (Game) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = games, key = { it.id }) { game ->
            GameItemModern(
                game = game,
                onDeleteClick = { onDeleteClick(game) }
            )
        }
    }
}

@Composable
fun GameItemModern(game: Game, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = BorderNeonGold,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del juego
            AsyncImage(
                model = game.imagenUrl,
                contentDescription = "Carátula de ${game.nombre}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = BorderMagenta,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = HomePrimary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🎮 ${game.plataforma}",
                            style = MaterialTheme.typography.bodySmall,
                            color = HomeSecondary
                        )
                        Text(
                            text = "⏱️ ${game.horasJugadas}h",
                            style = MaterialTheme.typography.bodySmall,
                            color = HomeAccent
                        )
                    }

                    Text(
                        text = "⭐ ${game.calificacion}/10",
                        style = MaterialTheme.typography.bodyLarge,
                        color = HomePrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar juego",
                    tint = HomeSecondary
                )
            }
        }
    }
}