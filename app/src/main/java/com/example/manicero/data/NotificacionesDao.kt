package com.example.manicero.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.manicero.model.Notificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Insert
    suspend fun insertar(notificacion: Notificacion): Long

    @Query("SELECT * FROM notificaciones WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun observar(usuarioId: Int): Flow<List<Notificacion>>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE usuarioId = :usuarioId AND leida = 0")
    fun contarNoLeidas(usuarioId: Int): Flow<Int>

    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarLeida(id: Int)

    @Query("UPDATE notificaciones SET leida = 1 WHERE usuarioId = :usuarioId")
    suspend fun marcarTodas(usuarioId: Int)
}