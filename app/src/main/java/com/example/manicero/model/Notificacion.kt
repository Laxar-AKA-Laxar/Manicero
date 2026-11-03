package com.example.manicero.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "notificaciones", indices = [Index(value = ["usuarioId"])])
data class Notificacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val titulo: String,
    val mensaje: String,
    val fecha: Long,
    val leida: Boolean = false,
    val destino: String
)