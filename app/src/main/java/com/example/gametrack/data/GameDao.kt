package com.example.gametrack.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM Game WHERE ownerUserId = :userId")
    fun getGamesForUser(userId: Int): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)
}