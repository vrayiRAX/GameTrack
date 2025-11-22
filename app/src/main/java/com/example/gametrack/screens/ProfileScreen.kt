package com.example.gametrack.screens

import android.Manifest
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.gametrack.GameViewModel
import com.example.gametrack.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import androidx.compose.foundation.BorderStroke
import androidx.core.content.FileProvider
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: GameViewModel) {
    val userData by viewModel.currentUser.collectAsState()
    val games by viewModel.games.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val context = LocalContext.current

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraFile by remember { mutableStateOf<File?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showCameraErrorDialog by remember { mutableStateOf(false) }
    var cameraErrorMessage by remember { mutableStateOf("") }

    // Calcular estadísticas
    val totalJuegos = games.size
    val totalHoras = games.map { it.horasJugadas }.sum()
    val promedioCalificacion = if (games.isNotEmpty()) {
        val sumaCalificaciones = games.map { it.calificacion }.sum()
        sumaCalificaciones / games.size
    } else {
        0f
    }

    val juegoMasJugado = games.maxByOrNull { it.horasJugadas }
    val juegoMejorCalificado = games.maxByOrNull { it.calificacion }

    fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempCameraFile != null) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    tempCameraFile!!
                )
                viewModel.setProfileImage(uri)
            } else {
                tempCameraFile?.delete()
                tempCameraFile = null
                cameraErrorMessage = "No se pudo capturar la foto"
                showCameraErrorDialog = true
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = createImageFile()
            if (file != null) {
                tempCameraFile = file
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                try {
                    cameraLauncher.launch(uri)
                } catch (e: Exception) {
                    cameraErrorMessage = "Error al abrir cámara: ${e.message}"
                    showCameraErrorDialog = true
                }
            } else {
                cameraErrorMessage = "No se pudo crear el archivo"
                showCameraErrorDialog = true
            }
        } else {
            showPermissionDialog = true
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.setProfileImage(it)
            }
        }
    )

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("⚠️ Permiso requerido", color = ProfilePrimary) },
            text = {
                Column {
                    Text("Para tomar fotos, necesitamos acceso a la cámara.", color = TextPrimary)
                    Text("Puedes habilitarlo en Configuración > Apps > GameTrack > Permisos",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPermissionDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProfileButton,
                        contentColor = DarkBackground
                    )
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    if (showCameraErrorDialog) {
        AlertDialog(
            onDismissRequest = { showCameraErrorDialog = false },
            title = { Text("❌ Error con la cámara", color = ProfilePrimary) },
            text = {
                Column {
                    Text(cameraErrorMessage, color = TextPrimary)
                    Text("Intenta nuevamente o usa la galería.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCameraErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProfileButton,
                        contentColor = DarkBackground
                    )
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = {
                Text("🖼️ Seleccionar imagen de perfil", color = ProfilePrimary)
            },
            text = {
                Column {
                    Text("¿De dónde quieres obtener tu foto de perfil?", color = TextPrimary)
                }
            },
            confirmButton = {
                Column {
                    Button(
                        onClick = {
                            showImageSourceDialog = false
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProfileButton,
                            contentColor = DarkBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Galería",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("📁 Abrir galería")
                    }

                    Button(
                        onClick = {
                            showImageSourceDialog = false
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProfileAccent,
                            contentColor = DarkBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cámara",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("📸 Tomar foto ahora")
                    }
                }
            },
            dismissButton = {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { showImageSourceDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("❌ Cancelar", color = ProfileSecondary)
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "👤 Mi Perfil",
                        style = MaterialTheme.typography.titleLarge,
                        color = ProfilePrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = ProfilePrimary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = ProfilePrimary
                        )
                    }
                }
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 24.dp)
            ) {
                if (profileImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUri),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        color = CardDark,
                        border = BorderStroke(
                            width = 2.dp,
                            color = ProfileSecondary
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "👤",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = ProfileSecondary
                                )
                                Text(
                                    "Sin foto",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ProfileSecondary,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showImageSourceDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(40.dp),
                    containerColor = ProfileButton
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = DarkBackground
                    )
                }
            }

            if (profileImageUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "✅ Foto de perfil guardada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ProfileAccent,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "La imagen se mantendrá durante el uso de la app",
                            style = MaterialTheme.typography.bodySmall,
                            color = ProfileSecondary
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "📋 Información del perfil",
                        style = MaterialTheme.typography.titleMedium,
                        color = ProfilePrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "👤 Nombre de usuario:",
                                style = MaterialTheme.typography.bodySmall,
                                color = ProfileSecondary
                            )
                            Text(
                                userData?.name ?: "No establecido",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProfilePrimary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "📧 Correo electrónico:",
                                style = MaterialTheme.typography.bodySmall,
                                color = ProfileSecondary
                            )
                            Text(
                                userData?.email ?: "No establecido",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProfilePrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "📊 Mis Estadísticas de Juegos",
                        style = MaterialTheme.typography.titleMedium,
                        color = ProfilePrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "🎮 Juegos en mi lista:",
                            color = ProfileSecondary
                        )
                        Text(
                            "$totalJuegos",
                            color = ProfileAccent,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "⏱️ Horas totales jugadas:",
                            color = ProfileSecondary
                        )
                        Text(
                            "$totalHoras horas",
                            color = ProfileAccent,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "⭐ Calificación promedio:",
                            color = ProfileSecondary
                        )
                        Text(
                            "⭐ ${String.format("%.1f", promedioCalificacion)}/10",
                            color = ProfileAccent,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Juego más jugado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "🔥 Juego más jugado:",
                            color = ProfileSecondary
                        )
                        Text(
                            juegoMasJugado?.nombre ?: "Ninguno",
                            color = ProfileAccent,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "🏆 Juego favorito:",
                            color = ProfileSecondary
                        )
                        Text(
                            juegoMejorCalificado?.nombre ?: "Ninguno",
                            color = ProfileAccent,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProfileButton,
                    contentColor = DarkBackground
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    "🚀 Volver al Inicio",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}