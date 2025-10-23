package com.example.gametrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.gametrack.data.Game
import com.example.gametrack.data.GameDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application.applicationContext,
        GameDatabase::class.java,
        "game_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val gameDao = db.gameDao()

    val games: StateFlow<List<Game>> = gameDao.getAllGames()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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