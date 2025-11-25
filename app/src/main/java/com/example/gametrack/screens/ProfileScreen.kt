package com.example.gametrack.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gametrack.GameViewModel
import com.example.gametrack.ui.theme.NeonGreen
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

val AmberAccent = Color(0xFFFFB300)
val CardBackground = Color(0xFF1E1E1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: GameViewModel
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val games by viewModel.games.collectAsState()

    // Estadísticas
    val totalJuegos = games.size
    val totalHoras = games.sumOf { it.horasJugadas }
    val promedioCalificacion = if (games.isNotEmpty()) {
        games.map { it.calificacion }.average()
    } else { 0.0 }
    val juegoMasJugado = if (games.isNotEmpty()) {
        games.maxByOrNull { it.horasJugadas }?.nombre ?: "Ninguno"
    } else "Ninguno"
    val juegoFavorito = if (games.isNotEmpty()) {
        games.maxByOrNull { it.calificacion }?.nombre ?: "Ninguno"
    } else "Ninguno"

    var username by remember(currentUser) { mutableStateOf(currentUser?.username ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Cargar imagen guardada al iniciar
    LaunchedEffect(currentUser) {
        currentUser?.fotoPerfilUri?.let { path ->
            val file = File(path)
            if (file.exists()) {
                imageUri = Uri.fromFile(file)
            }
        }
    }

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUri = it }
    }

    // Resultado de la Cámara
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempCameraUri != null) imageUri = tempCameraUri
    }

    //Permiso de Cámara
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            try {
                val uri = createImageFileUri(context)
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Error al iniciar cámara: Verifica el AndroidManifest.xml",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO DE PERFIL
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                val displayName = if (username.isNotEmpty()) username else "U"
                AsyncImage(
                    model = imageUri ?: "https://ui-avatars.com/api/?name=$displayName&background=0D8&color=fff&size=256",
                    contentDescription = "Foto",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, NeonGreen, CircleShape)
                        .clickable { showImageSourceDialog = true },
                    contentScale = ContentScale.Crop
                )

                SmallFloatingActionButton(
                    onClick = { showImageSourceDialog = true },
                    containerColor = NeonGreen,
                    contentColor = Color.Black,
                    modifier = Modifier.size(35.dp)
                ) {
                    Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                }
            }

            InfoCard(title = "Información del perfil", icon = Icons.Default.Person) {
                ProfileField(
                    label = "Nombre de usuario:",
                    value = username,
                    icon = Icons.Default.Face,
                    onValueChange = { username = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileField(
                    label = "Correo electrónico:",
                    value = currentUser?.email ?: "",
                    icon = Icons.Default.Email,
                    isReadOnly = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ESTADÍSTICAS
            InfoCard(title = "Mis Estadísticas de Juegos", icon = Icons.Default.BarChart) {
                StatRow(icon = Icons.Default.Gamepad, label = "Juegos en mi lista:", value = "$totalJuegos")
                StatRow(icon = Icons.Default.Timer, label = "Horas totales jugadas:", value = "$totalHoras horas")
                StatRow(icon = Icons.Default.Star, label = "Calificación promedio:", value = String.format("%.1f/10", promedioCalificacion))
                StatRow(icon = Icons.Default.Whatshot, label = "Juego más jugado:", value = juegoMasJugado)
                StatRow(icon = Icons.Default.EmojiEvents, label = "Juego favorito:", value = juegoFavorito)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    viewModel.updateUserProfile(username, imageUri, context)
                    Toast.makeText(context, "Perfil guardado", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Guardar y Volver al Inicio",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Cambiar foto") },
            text = { Text("Elige una opción") },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showImageSourceDialog = false }) { Text("Cancelar") } },
            icon = {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { galleryLauncher.launch("image/*"); showImageSourceDialog = false }) {
                        Icon(Icons.Default.Image, "Galería")
                    }
                    IconButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA); showImageSourceDialog = false }) {
                        Icon(Icons.Default.CameraAlt, "Cámara")
                    }
                }
            }
        )
    }
}

//COMPONENTES AUXILIARES

@Composable
fun InfoCard(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = AmberAccent, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = AmberAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun ProfileField(label: String, value: String, icon: ImageVector, isReadOnly: Boolean = false, onValueChange: (String) -> Unit = {}) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.Red.copy(alpha = 0.8f), fontSize = 14.sp)
        }
        if (isReadOnly) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 24.dp, top = 4.dp)
            )
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = NeonGreen,
                    focusedIndicatorColor = NeonGreen,
                    unfocusedIndicatorColor = Color.DarkGray
                ),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            )
        }
    }
}

@Composable
fun StatRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = Color.Red.copy(alpha = 0.8f), fontSize = 15.sp)
        }
        Text(
            text = value,
            color = AmberAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

// Función auxiliar
fun createImageFileUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.cacheDir
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    val authority = "${context.packageName}.provider"
    return FileProvider.getUriForFile(context, authority, file)
}