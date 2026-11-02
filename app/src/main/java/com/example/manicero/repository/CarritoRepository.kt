package com.example.manicero.repository

import com.example.manicero.data.CarritoDao
import com.example.manicero.model.ItemCarrito
import com.example.manicero.model.ItemCarritoDetalle
import com.example.manicero.model.Producto
import kotlinx.coroutines.flow.Flow

class CarritoRepository(private val dao: CarritoDao) {

    fun items(usuarioId: Int): Flow<List<ItemCarritoDetalle>> = dao.observar(usuarioId)

    suspend fun agregar(usuarioId: Int, producto: Producto, cantidad: Int): Boolean {
        val existente = dao.buscar(usuarioId, producto.id)
        val total = (existente?.cantidad ?: 0) + cantidad
        if (total > producto.stock) return false
        dao.guardar(
            ItemCarrito(
                id = existente?.id ?: 0,
                usuarioId = usuarioId,
                productoId = producto.id,
                cantidad = total
            )
        )
        return true
    }

    suspend fun cambiarCantidad(usuarioId: Int, productoId: Int, cantidad: Int) =
        dao.actualizarCantidad(usuarioId, productoId, cantidad)

    suspend fun quitar(usuarioId: Int, productoId: Int) = dao.quitar(usuarioId, productoId)

    suspend fun vaciar(usuarioId: Int) = dao.vaciar(usuarioId)
}