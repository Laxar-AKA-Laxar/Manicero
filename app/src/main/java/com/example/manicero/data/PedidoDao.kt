package com.example.manicero.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.manicero.model.Pedido
import com.example.manicero.model.PedidoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Insert
    suspend fun insertarPedido(pedido: Pedido): Long

    @Insert
    suspend fun insertarItems(items: List<PedidoItem>)

    @Query("SELECT * FROM pedidos WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun observarPorUsuario(usuarioId: Int): Flow<List<Pedido>>

    @Query("SELECT * FROM pedidos ORDER BY fecha DESC")
    fun observarTodos(): Flow<List<Pedido>>

    @Query("SELECT * FROM pedido_items WHERE pedidoId = :pedidoId")
    fun observarItems(pedidoId: Int): Flow<List<PedidoItem>>

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)
}