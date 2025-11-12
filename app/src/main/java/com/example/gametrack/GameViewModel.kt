package com.example.gametrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.gametrack.data.Game
import com.example.gametrack.data.GameDatabase
import com.example.gametrack.data.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.MessageDigest

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application.applicationContext,
        GameDatabase::class.java,
        "game_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val gameDao = db.gameDao()
    private val userDao = db.userDao()

    private val _currentUser = MutableStateFlow<User?>(null)

    val games: StateFlow<List<Game>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            gameDao.getGamesForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addGame(nombre: String, plataforma: String, horas: Int, calificacion: Float, imagenUrl: String?) {
        viewModelScope.launch {
            val userId = _currentUser.value?.id ?: return@launch

            val newGame = Game(
                ownerUserId = userId,
                nombre = nombre,
                plataforma = plataforma,
                horasJugadas = horas,
                calificacion = calificacion,
                imagenUrl = imagenUrl
            )
            gameDao.insertGame(newGame)
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            gameDao.deleteGame(game)
        }
    }

    suspend fun loginUser(username: String, pass: String): Boolean {
        val user = userDao.getUserByUsername(username)
        if (user != null) {
            if (user.passHash == hashPassword(pass)) {
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
            val passHash = hashPassword(pass)
            val newUser = User(username = username, email = email, passHash = passHash)
            userDao.insertUser(newUser)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}