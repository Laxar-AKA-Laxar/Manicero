package com.example.manicero.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "carrito",
    indices = [Index(value = ["usuarioId", "productoId"], unique = true)]
)
data class ItemCarrito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val productoId: Int,
    val cantidad: Int
)