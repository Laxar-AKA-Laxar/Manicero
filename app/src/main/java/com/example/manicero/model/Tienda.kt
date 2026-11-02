package com.example.manicero.model

data class Tienda(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val direccion: String,
    val horario: String,
    val latitud: Double,
    val longitud: Double
)