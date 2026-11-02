package com.example.manicero.repository

import androidx.room.withTransaction
import com.example.manicero.data.AppDatabase
import com.example.manicero.model.EstadoPedido
import com.example.manicero.model.Pedido
import com.example.manicero.model.PedidoItem
import kotlinx.coroutines.flow.Flow

class StockInsuficienteException(val producto: String) : Exception()

class PedidoRepository(private val base: AppDatabase) {

    private val pedidos = base.pedidoDao()
    private val carrito = base.carritoDao()
    private val productos = base.productoDao()

    fun delUsuario(usuarioId: Int): Flow<List<Pedido>> = pedidos.observarPorUsuario(usuarioId)

    fun todos(): Flow<List<Pedido>> = pedidos.observarTodos()

    fun items(pedidoId: Int): Flow<List<PedidoItem>> = pedidos.observarItems(pedidoId)

    suspend fun cambiarEstado(id: Int, estado: String) = pedidos.actualizarEstado(id, estado)

    suspend fun confirmar(usuarioId: Int, entrega: String): Long = base.withTransaction {
        val lista = carrito.listar(usuarioId)
        if (lista.isEmpty()) throw IllegalStateException("carrito vacio")
        lista.forEach { item ->
            if (productos.descontarStock(item.productoId, item.cantidad) == 0) {
                throw StockInsuficienteException(item.nombre)
            }
        }
        val id = pedidos.insertarPedido(
            Pedido(
                usuarioId = usuarioId,
                fecha = System.currentTimeMillis(),
                total = lista.sumOf { it.precio * it.cantidad },
                estado = EstadoPedido.PENDIENTE,
                entrega = entrega
            )
        )
        pedidos.insertarItems(
            lista.map {
                PedidoItem(
                    pedidoId = id.toInt(),
                    productoId = it.productoId,
                    nombre = it.nombre,
                    precio = it.precio,
                    cantidad = it.cantidad
                )
            }
        )
        carrito.vaciar(usuarioId)
        id
    }
}