package com.example.gametrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gametrack.GameViewModel
import com.example.gametrack.data.Game
import com.example.gametrack.ui.theme.NeonGreen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(navController: NavController, viewModel: GameViewModel) {
    var nombre by remember { mutableStateOf("") }
    var plataforma by remember { mutableStateOf("") }
    var horas by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf<String?>(null) }

    //  Este bloque busca la imagen real en la API
    LaunchedEffect(nombre) {
        if (nombre.length > 2) {
            delay(600) // Espera un poco mientras el usuario escribe

            // Esta es la llamada real a tu ApiGame.kt
            IGDBService.buscarCaratula(nombre) { url ->
                imagenUrl = url
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Añadir Juego",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonGreen
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Juego") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = plataforma,
                onValueChange = { plataforma = it },
                label = { Text("Plataforma") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = horas,
                onValueChange = { horas = it },
                label = { Text("Horas Jugadas") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = calificacion,
                onValueChange = { calificacion = it },
                label = { Text("Calificación (0-10)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            imagenUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Carátula del juego",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (nombre.isNotBlank() && plataforma.isNotBlank() && horas.isNotBlank() && calificacion.isNotBlank()) {
                        viewModel.addGame(
                            Game(
                                nombre = nombre,
                                plataforma = plataforma,
                                horasJugadas = horas.toIntOrNull() ?: 0,
                                calificacion = calificacion.toFloatOrNull() ?: 0f,
                                imagenUrl = imagenUrl
                            )
                        )
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Guardar Juego", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}