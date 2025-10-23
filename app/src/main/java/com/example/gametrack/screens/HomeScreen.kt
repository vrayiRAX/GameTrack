package com.example.gametrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.gametrack.GameViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gametrack.ui.theme.NeonGreen
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: GameViewModel) {
    val games by viewModel.games.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "GameTrack",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonGreen
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add") },
                containerColor = NeonGreen
            ) {
                Text("+", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        if (games.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay juegos en tu lista.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(items = games, key = { it.id }) { game ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagen (como la tenías)
                        AsyncImage(
                            model = game.imagenUrl,
                            contentDescription = "Carátula de ${game.nombre}",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(end = 16.dp)
                        )

                        // Tarjeta con la info
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
                                        viewModel.deleteGame(game)
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd) // Lo pone arriba a la derecha
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar juego",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant // Color del ícono
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