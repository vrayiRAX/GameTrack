package com.example.gametrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey
    val email: String,
    val nombre: String,
    val profileImage: String? = null
)