package com.example.manicero.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.manicero.model.Usuario

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE alias = :alias LIMIT 1")
    suspend fun buscarPorAlias(alias: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Usuario?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario): Long
}