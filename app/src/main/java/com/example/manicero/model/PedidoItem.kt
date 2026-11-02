package com.example.manicero.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "pedido_items", indices = [Index(value = ["pedidoId"])])
data class PedidoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pedidoId: Int,
    val productoId: Int,
    val nombre: String,
    val precio: Int,
    val cantidad: Int
)