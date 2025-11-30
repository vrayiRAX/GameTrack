package com.example.gametrack.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Game::class, User::class],
    version = 8
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun userDao(): UserDao
}