package com.example.gametrack

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.gametrack.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val gameRepository: GameRepository
    private val userRepository: UserRepository
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        val db = Room.databaseBuilder(
            application.applicationContext,
            GameDatabase::class.java,
            "game_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        val gameDao = db.gameDao()
        val userDao = db.userDao()

        gameRepository = GameRepository(gameDao)
        userRepository = UserRepository(userDao)
    }

    val games: StateFlow<List<Game>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            gameRepository.getGamesForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addGame(nombre: String, plataforma: String, horas: Int, calificacion: Float, imagenUrl: String?) {
        viewModelScope.launch {
            val userId = _currentUser.value?.id ?: return@launch

            //RETRASO ANIMACIÓN
            delay(1500)

            val newGame = Game(
                ownerUserId = userId,
                nombre = nombre,
                plataforma = plataforma,
                horasJugadas = horas,
                calificacion = calificacion,
                imagenUrl = imagenUrl
            )
            gameRepository.insertGame(newGame)
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            gameRepository.deleteGame(game)
        }
    }

    suspend fun loginUser(username: String, pass: String): Boolean {
        val user = userRepository.getUserByUsername(username)
        if (user != null) {
            if (user.passHash == userRepository.getPasswordHash(pass)) {
                _currentUser.value = user
                return true
            }
        }
        _currentUser.value = null
        return false
    }

    fun logout() {
        _currentUser.value = null
    }

    fun registerUser(username: String, pass: String, email: String) {
        viewModelScope.launch {
            userRepository.registerUser(username, pass, email)
        }
    }

    fun updateUserProfile(newName: String, imageUri: Uri?, context: Context) {
        val current = _currentUser.value ?: return

        viewModelScope.launch {
            var finalUriString = current.fotoPerfilUri
            if (imageUri != null) {
                finalUriString = copyImageToInternalStorage(context, imageUri, current.id)
            }

            val updatedUser = current.copy(
                username = newName,
                fotoPerfilUri = finalUriString
            )

            userRepository.updateUser(updatedUser)
            _currentUser.value = updatedUser
        }
    }
    private fun copyImageToInternalStorage(context: Context, uri: Uri, userId: Int): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "profile_$userId.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}