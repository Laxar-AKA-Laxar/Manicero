package com.example.manicero.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios", indices = [Index(value = ["alias"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val alias: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val claveHash: String,
    val rol: String
)