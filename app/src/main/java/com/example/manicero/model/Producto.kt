package com.example.manicero.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    val imagenUri: String? = null,
    val codigoQr: String
)