package com.example.gametrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.gametrack.data.Game
import com.example.gametrack.data.GameDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserData(
    val name: String = "",
    val email: String = ""
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application.applicationContext,
        GameDatabase::class.java,
        "game_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val gameDao = db.gameDao()

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser.asStateFlow()

    private val _profileImageUri = MutableStateFlow<android.net.Uri?>(null)
    val profileImageUri: StateFlow<android.net.Uri?> = _profileImageUri.asStateFlow()

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            gameDao.getAllGames().collect { gameList ->
                _games.value = gameList
            }
        }
    }

    fun loadGamesOrderedByNameAsc() {
        viewModelScope.launch {
            gameDao.getGamesOrderedByNameAsc().collect { gameList ->
                _games.value = gameList
            }
        }
    }

    fun loadGamesOrderedByNameDesc() {
        viewModelScope.launch {
            gameDao.getGamesOrderedByNameDesc().collect { gameList ->
                _games.value = gameList
            }
        }
    }

    fun loadGamesOrderedByHoursDesc() {
        viewModelScope.launch {
            gameDao.getGamesOrderedByHoursDesc().collect { gameList ->
                _games.value = gameList
            }
        }
    }

    fun loadGamesOrderedByRatingDesc() {
        viewModelScope.launch {
            gameDao.getGamesOrderedByRatingDesc().collect { gameList ->
                _games.value = gameList
            }
        }
    }

    fun loadGamesDefault() {
        loadGames()
    }

    fun setUser(email: String, name: String) {
        _currentUser.value = UserData(name = name, email = email)
    }

    fun clearUser() {
        _currentUser.value = null
    }

    fun setProfileImage(uri: android.net.Uri?) {
        _profileImageUri.value = uri
    }

    fun addGame(game: Game) {
        viewModelScope.launch {
            gameDao.insertGame(game)
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            gameDao.deleteGame(game)
        }
    }
}