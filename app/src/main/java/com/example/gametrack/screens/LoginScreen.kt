package com.example.gametrack.screens

import android.os.Vibrator
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gametrack.GameViewModel
import com.example.gametrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: GameViewModel) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var nombreError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun vibrate() { vibrator.vibrate(100) }

    fun isValidNombre(input: String): Boolean {
        val regex = "^[\\p{L} .'-]+$".toRegex()
        return regex.matches(input) && input.isNotBlank() && input.length >= 2
    }

    fun hasNumbers(input: String): Boolean = input.any { it.isDigit() }

    fun isValidEmail(input: String): Boolean {
        return input.contains("@gmail.com") && input.isNotBlank() && input.length > 10
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "🎮 Iniciar Sesión",
                        style = MaterialTheme.typography.titleLarge,
                        color = LoginPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = LoginPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🚀 Bienvenido a GameTrack",
                style = MaterialTheme.typography.headlineMedium,
                color = LoginPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Registra y organiza tus juegos favoritos",
                style = MaterialTheme.typography.bodyMedium,
                color = LoginSecondary,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Campo de nombre
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👤 Tu nombre",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LoginPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            if (showError) {
                                nombreError = !isValidNombre(nombre)
                            }
                        },
                        label = { Text("Ej: Juan Pérez", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ingresa tu nombre completo", color = TextMuted) },
                        singleLine = true,
                        isError = nombreError,
                        supportingText = {
                            if (nombreError) {
                                if (hasNumbers(nombre)) {
                                    Text(
                                        "❌ Carácteres Inválidos, Ingrese el nombre correctamente",
                                        color = LoginAccent
                                    )
                                } else if (nombre.length < 2) {
                                    Text(
                                        "❌ El nombre debe tener al menos 2 caracteres",
                                        color = LoginAccent
                                    )
                                } else if (nombre.isBlank()) {
                                    Text(
                                        "❌ El nombre es obligatorio",
                                        color = LoginAccent
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // Campo de email
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📧 Correo electrónico",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LoginPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (showError) {
                                emailError = !isValidEmail(email)
                            }
                        },
                        label = { Text("Ej: juan@gmail.com", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ingresa tu correo Gmail", color = TextMuted) },
                        singleLine = true,
                        isError = emailError,
                        supportingText = {
                            if (emailError) {
                                Text(
                                    "❌ El email debe contener '@gmail.com'",
                                    color = LoginAccent
                                )
                            }
                        }
                    )
                }
            }

            // Mensajes de error
            if (showError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepViolet)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (nombre.isBlank() || email.isBlank()) {
                            Text(
                                text = "⚠️ Por favor, completa todos los campos",
                                color = LoginAccent,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else if (nombreError || emailError) {
                            Text(
                                text = "⚠️ Corrige los errores antes de continuar",
                                color = LoginAccent,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val isNombreValid = isValidNombre(nombre)
                    val isEmailValid = isValidEmail(email)

                    showError = true
                    nombreError = !isNombreValid
                    emailError = !isEmailValid

                    if (isNombreValid && isEmailValid) {
                        viewModel.setUser(email, nombre)
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        vibrate()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoginButton,
                    contentColor = DarkBackground
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = "🎯 Comenzar",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 Solo necesitas ingresar tus datos una vez",
                        style = MaterialTheme.typography.bodySmall,
                        color = LoginSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}