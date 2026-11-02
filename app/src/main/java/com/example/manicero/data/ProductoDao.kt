package com.example.manicero.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.manicero.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre")
    fun obtenerTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    fun observarPorId(id: Int): Flow<Producto?>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Producto?

    @Query("SELECT * FROM productos WHERE codigoQr = :codigo LIMIT 1")
    suspend fun buscarPorQr(codigo: String): Producto?

    @Query("UPDATE productos SET stock = stock - :cantidad WHERE id = :id AND stock >= :cantidad")
    suspend fun descontarStock(id: Int, cantidad: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<Producto>)

    @Update
    suspend fun actualizar(producto: Producto)
}