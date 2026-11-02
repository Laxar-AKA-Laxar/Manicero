package com.example.manicero.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.manicero.model.ItemCarrito
import com.example.manicero.model.ItemCarritoDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    @Query(
        "SELECT p.id AS productoId, p.nombre AS nombre, p.precio AS precio, " +
                "p.stock AS stock, c.cantidad AS cantidad " +
                "FROM carrito c INNER JOIN productos p ON p.id = c.productoId " +
                "WHERE c.usuarioId = :usuarioId ORDER BY p.nombre"
    )
    fun observar(usuarioId: Int): Flow<List<ItemCarritoDetalle>>

    @Query(
        "SELECT p.id AS productoId, p.nombre AS nombre, p.precio AS precio, " +
                "p.stock AS stock, c.cantidad AS cantidad " +
                "FROM carrito c INNER JOIN productos p ON p.id = c.productoId " +
                "WHERE c.usuarioId = :usuarioId ORDER BY p.nombre"
    )
    suspend fun listar(usuarioId: Int): List<ItemCarritoDetalle>

    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId AND productoId = :productoId LIMIT 1")
    suspend fun buscar(usuarioId: Int, productoId: Int): ItemCarrito?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(item: ItemCarrito)

    @Query("UPDATE carrito SET cantidad = :cantidad WHERE usuarioId = :usuarioId AND productoId = :productoId")
    suspend fun actualizarCantidad(usuarioId: Int, productoId: Int, cantidad: Int)

    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId AND productoId = :productoId")
    suspend fun quitar(usuarioId: Int, productoId: Int)

    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun vaciar(usuarioId: Int)
}