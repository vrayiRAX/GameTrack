package com.example.gametrack.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM Game")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM Game ORDER BY nombre ASC")
    fun getGamesOrderedByNameAsc(): Flow<List<Game>>

    @Query("SELECT * FROM Game ORDER BY nombre DESC")
    fun getGamesOrderedByNameDesc(): Flow<List<Game>>

    @Query("SELECT * FROM Game ORDER BY horasJugadas DESC")
    fun getGamesOrderedByHoursDesc(): Flow<List<Game>>

    @Query("SELECT * FROM Game ORDER BY calificacion DESC")
    fun getGamesOrderedByRatingDesc(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)
}