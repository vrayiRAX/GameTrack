package com.example.gametrack.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gametrack.GameViewModel
import com.example.gametrack.ui.theme.NeonGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    navController: NavController,
    viewModel: GameViewModel
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf<String?>(null) }
    var horas by remember { mutableStateOf("") }
    var calificacion by remember { mutableFloatStateOf(5f) }
    val platforms = listOf("PC", "PlayStation", "Xbox", "Nintendo", "Celular")
    var selectedPlatform by remember { mutableStateOf(platforms[0]) }
    var expanded by remember { mutableStateOf(false) }
    var nombreError by remember { mutableStateOf<String?>(null) }
    var horasError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) } // Estado de carga
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(nombre) {
        if (nombre.length > 2) {
            delay(600)
            IGDBService.buscarCaratula(nombre) { url ->
                imagenUrl = url
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Juego") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Añadir a la Biblioteca",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Nombre del Juego
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    if (it.isNotBlank()) nombreError = null
                },
                label = { Text("Nombre del Juego") },
                placeholder = { Text("Ej: The Legend of Zelda") },
                leadingIcon = {
                    Icon(Icons.Default.Gamepad, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                isError = nombreError != null,
                supportingText = {
                    if (nombreError != null) Text(nombreError!!, color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Plataforma
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedPlatform,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Plataforma") },
                    leadingIcon = {
                        Icon(Icons.Default.Computer, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    platforms.forEach { platform ->
                        DropdownMenuItem(
                            text = { Text(platform) },
                            onClick = {
                                selectedPlatform = platform
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Horas Jugadas
            OutlinedTextField(
                value = horas,
                onValueChange = { newValue ->
                    val filteredValue = newValue.filter { it.isDigit() }
                    horas = filteredValue
                    horasError = when {
                        filteredValue.isEmpty() -> null
                        filteredValue.toIntOrNull() == null -> "Debe ser un número entero"
                        filteredValue.toInt() < 0 -> "Debe ser positivo"
                        else -> null
                    }
                },
                label = { Text("Horas Jugadas") },
                placeholder = { Text("Ej: 45") },
                leadingIcon = {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = horasError != null,
                supportingText = {
                    if (horasError != null) Text(horasError!!, color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calificación",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${calificacion.toInt()}/10",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = calificacion,
                    onValueChange = { calificacion = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGreen,
                        activeTrackColor = NeonGreen
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // VISTA DE LA IMAGEN
            imagenUrl?.let { url ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .aspectRatio(0.7f),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = "Carátula del juego",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            //BOTÓN GUARDAR
            Button(
                onClick = {
                    keyboardController?.hide()

                    // Reiniciar errores
                    nombreError = null
                    horasError = null
                    var isValid = true

                    // Validación de Nombre
                    if (nombre.isBlank()) {
                        nombreError = "El nombre es obligatorio"
                        isValid = false
                    }

                    // Validación de Horas
                    val horasInt = horas.toIntOrNull()
                    if (horas.isBlank() || horasInt == null || horasInt < 0) {
                        horasError = "Horas inválidas (debe ser número entero positivo)"
                        isValid = false
                    }

                    if (isValid) {
                        scope.launch {
                            isLoading = true

                            viewModel.addGame(
                                nombre = nombre,
                                plataforma = selectedPlatform,
                                horas = horasInt!!,
                                calificacion = calificacion,
                                imagenUrl = imagenUrl
                            )

                            isLoading = false
                            Toast.makeText(context, "Juego añadido exitosamente", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(context, "Por favor, corrige los campos marcados", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Juego", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}