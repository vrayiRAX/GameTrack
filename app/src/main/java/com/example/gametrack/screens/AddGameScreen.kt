package com.example.gametrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gametrack.GameViewModel
import com.example.gametrack.data.Game
import com.example.gametrack.ui.theme.NeonGreen
import kotlinx.coroutines.delay
import androidx.compose.foundation.text.KeyboardOptions
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(navController: NavController, viewModel: GameViewModel) {
    var nombre by remember { mutableStateOf("") }
    var plataforma by remember { mutableStateOf("") }
    var horas by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf<String?>(null) }

    var showError by remember { mutableStateOf(false) }
    var nombreError by remember { mutableStateOf(false) }
    var plataformaError by remember { mutableStateOf(false) }
    var horasError by remember { mutableStateOf(false) }
    var calificacionError by remember { mutableStateOf(false) }
    var isLoadingImage by remember { mutableStateOf(false) }

    LaunchedEffect(nombre) {
        if (nombre.length > 2) {
            isLoadingImage = true
            delay(800)

            IGDBService.buscarCaratula(nombre) { url ->
                imagenUrl = url
                isLoadingImage = false
            }
        } else {
            imagenUrl = null
            isLoadingImage = false
        }
    }

    fun isValidNombreJuego(input: String): Boolean {
        return input.isNotBlank() && input.length >= 2
    }

    fun isValidPlataforma(input: String): Boolean {
        val regex = "^[\\p{L} .'-]+$".toRegex()
        return regex.matches(input) && input.isNotBlank()
    }

    fun isValidHoras(input: String): Boolean {
        return input.isNotBlank() && input.toIntOrNull() != null && input.toInt() >= 0
    }

    fun isValidCalificacion(input: String): Boolean {
        val numero = input.toFloatOrNull()
        return input.isNotBlank() && numero != null && numero >= 0 && numero <= 10
    }

    fun filterPlataforma(input: String): String {
        return input.filter {
            it.isLetter() ||
                    it.isWhitespace() ||
                    it == 'á' || it == 'é' || it == 'í' || it == 'ó' || it == 'ú' ||
                    it == 'Á' || it == 'É' || it == 'Í' || it == 'Ó' || it == 'Ú' ||
                    it == 'ñ' || it == 'Ñ' || it == 'ü' || it == 'Ü' ||
                    it == '.' || it == '-' || it == '\'' || it == ' '
        }
    }

    fun filterHoras(input: String): String {
        return input.filter { it.isDigit() }
    }

    fun filterCalificacion(input: String): String {
        return input.filter { it.isDigit() || it == '.' }
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
                onValueChange = {
                    nombre = it
                    if (showError) {
                        nombreError = !isValidNombreJuego(nombre)
                    }
                },
                label = { Text("Nombre del Juego") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nombreError,
                supportingText = {
                    if (nombreError) {
                        Text(
                            "El nombre debe tener al menos 2 caracteres",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (nombre.length > 2) {
                        Text(
                            "Buscando carátula automáticamente...",
                            color = NeonGreen
                        )
                    }
                },
                placeholder = { Text("Ej: The Legend of Zelda") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = plataforma,
                onValueChange = {
                    plataforma = filterPlataforma(it)
                    if (showError) {
                        plataformaError = !isValidPlataforma(plataforma)
                    }
                },
                label = { Text("Plataforma") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = plataformaError,
                supportingText = {
                    if (plataformaError) {
                        Text(
                            "La plataforma solo puede contener letras",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                placeholder = { Text("Ej: Nintendo Switch") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = horas,
                onValueChange = {
                    horas = filterHoras(it)
                    if (showError) {
                        horasError = !isValidHoras(horas)
                    }
                },
                label = { Text("Horas Jugadas") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = horasError,
                supportingText = {
                    if (horasError) {
                        Text(
                            "Ingresa un número válido de horas",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                placeholder = { Text("Ej: 50") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = calificacion,
                onValueChange = {
                    calificacion = filterCalificacion(it)
                    if (showError) {
                        calificacionError = !isValidCalificacion(calificacion)
                    }
                },
                label = { Text("Calificación (0-10)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = calificacionError,
                supportingText = {
                    if (calificacionError) {
                        Text(
                            "La calificación debe ser un número entre 0 y 10",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                placeholder = { Text("Ej: 8.5") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoadingImage) {
                CircularProgressIndicator(
                    color = NeonGreen,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    "Buscando carátula...",
                    color = NeonGreen,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            imagenUrl?.let { url ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Carátula encontrada:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonGreen,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    AsyncImage(
                        model = url,
                        contentDescription = "Carátula de $nombre",
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (!isLoadingImage && imagenUrl == null && nombre.length > 2) {
                OutlinedTextField(
                    value = "",
                    onValueChange = { nuevaUrl ->
                        imagenUrl = if (nuevaUrl.isNotBlank()) nuevaUrl else null
                    },
                    label = { Text("URL de Imagen (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Pega aquí la URL si no se encontró carátula automática") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (showError && (nombreError || plataformaError || horasError || calificacionError)) {
                Text(
                    text = "Corrige los errores antes de guardar",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    val isNombreValid = isValidNombreJuego(nombre)
                    val isPlataformaValid = isValidPlataforma(plataforma)
                    val isHorasValid = isValidHoras(horas)
                    val isCalificacionValid = isValidCalificacion(calificacion)

                    showError = true
                    nombreError = !isNombreValid
                    plataformaError = !isPlataformaValid
                    horasError = !isHorasValid
                    calificacionError = !isCalificacionValid

                    if (isNombreValid && isPlataformaValid && isHorasValid && isCalificacionValid) {
                        val nuevoJuego = Game(
                            nombre = nombre,
                            plataforma = plataforma,
                            horasJugadas = horas.toInt(),
                            calificacion = calificacion.toFloat(),
                            imagenUrl = imagenUrl
                        )
                        viewModel.addGame(nuevoJuego)
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