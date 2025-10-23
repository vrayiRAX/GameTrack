package com.example.gametrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Game")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val plataforma: String,
    val horasJugadas: Int,
    val calificacion: Float,
    val imagenUrl: String? = null
)