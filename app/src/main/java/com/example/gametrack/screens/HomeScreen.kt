package com.example.gametrack.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.gametrack.GameViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gametrack.ui.theme.NeonGreen
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import com.example.gametrack.data.Game
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.gametrack.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: GameViewModel
) {
    val games by viewModel.games.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Defecto") }
    val sortedGames = remember(games, sortOption) {
        when (sortOption) {
            "Nombre (A-Z)" -> games.sortedBy { it.nombre.lowercase() }
            "Mejor Calificación" -> games.sortedByDescending { it.calificacion }
            "Más Horas" -> games.sortedByDescending { it.horasJugadas }
            else -> games
        }
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var gameToDelete by remember { mutableStateOf<Game?>(null) }
    var expanded by remember { mutableStateOf(false) }


    AnimatedVisibility(
        visible = showDeleteDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    gameToDelete = null
                },
                title = { Text("Eliminar Juego") },
                text = {
                    Text("¿Estás seguro de que quieres eliminar \"${gameToDelete?.nombre}\"?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            gameToDelete?.let { viewModel.deleteGame(it) }
                            showDeleteDialog = false
                            gameToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            gameToDelete = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }

    //DIÁLOGO DE CERRAR SESIÓN
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Quieres salir de tu cuenta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text("Salir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "GameTrack",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonGreen
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        val imageModel = if (currentUser?.fotoPerfilUri != null) {
                            File(currentUser!!.fotoPerfilUri!!)
                        } else {
                            "https://ui-avatars.com/api/?name=${currentUser?.username ?: "U"}&background=0D8&color=fff"
                        }

                        AsyncImage(
                            model = imageModel,
                            contentDescription = "Ir al perfil",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(1.dp, NeonGreen, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                actions = {
                    //BOTÓN DE FILTRAR_ORDENAR
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Ordenar lista",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Por Defecto") },
                                onClick = { sortOption = "Defecto"; showSortMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Nombre (A-Z)") },
                                onClick = { sortOption = "Nombre (A-Z)"; showSortMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Mejor Calificación") },
                                onClick = { sortOption = "Mejor Calificación"; showSortMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Más Horas Jugadas") },
                                onClick = { sortOption = "Más Horas"; showSortMenu = false }
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { expanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Cerrar Sesión")
                                },
                                onClick = {
                                    expanded = false
                                    showLogoutDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.ExitToApp,
                                        contentDescription = "Cerrar sesión"
                                    )
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
                containerColor = NeonGreen
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir juego",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Logo fondo
            Image(
                painter = painterResource(id = R.drawable.logo_gametrack),
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.Center)
                    .alpha(0.4f),
                contentScale = ContentScale.Fit
            )

            if (games.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "🎮 No hay juegos en tu lista.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NeonGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Pulsa el botón \"+\" para empezar a añadir tus juegos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = sortedGames, key = { it.id }) { game ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = game.imagenUrl,
                                contentDescription = "Carátula de ${game.nombre}",
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(end = 16.dp)
                            )

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(
                                            text = game.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Plataforma: ${game.plataforma}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Horas: ${game.horasJugadas}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.typography.bodyMedium.color
                                        )
                                        Text(
                                            text = "⭐ ${game.calificacion}/10",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = NeonGreen
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            gameToDelete = game
                                            showDeleteDialog = true
                                        },
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar juego",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}