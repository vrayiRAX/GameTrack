package com.example.gametrack.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {

    fun getGamesForUser(userId: Int): Flow<List<Game>> =
        gameDao.getGamesForUser(userId)

    suspend fun insertGame(game: Game) {
        gameDao.insertGame(game)
    }

    suspend fun deleteGame(game: Game) {
        gameDao.deleteGame(game)
    }
}