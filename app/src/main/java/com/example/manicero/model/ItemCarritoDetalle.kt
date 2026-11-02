package com.example.manicero.model

data class ItemCarritoDetalle(
    val productoId: Int,
    val nombre: String,
    val precio: Int,
    val stock: Int,
    val cantidad: Int
)