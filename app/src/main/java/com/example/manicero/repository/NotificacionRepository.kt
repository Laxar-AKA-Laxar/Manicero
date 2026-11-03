package com.example.manicero.repository

import com.example.manicero.data.AppDatabase
import com.example.manicero.data.Notificador
import com.example.manicero.model.Notificacion
import com.example.manicero.model.Pedido
import java.util.Locale
import kotlinx.coroutines.flow.Flow

class NotificacionRepository(base: AppDatabase, private val notificador: Notificador) {

    private val dao = base.notificacionDao()
    private val usuarios = base.usuarioDao()

    fun lista(usuarioId: Int): Flow<List<Notificacion>> = dao.observar(usuarioId)

    fun noLeidas(usuarioId: Int): Flow<Int> = dao.contarNoLeidas(usuarioId)

    suspend fun marcarLeida(id: Int) = dao.marcarLeida(id)

    suspend fun marcarTodas(usuarioId: Int) = dao.marcarTodas(usuarioId)

    suspend fun nuevoPedido(idPedido: Int) {
        val administrador = usuarios.buscarPorAlias("admin") ?: return
        registrar(
            administrador.id,
            "Nuevo pedido recibido",
            "Pedido " + codigo(idPedido) + " pendiente de validación"
        )
    }

    suspend fun estadoCambiado(pedido: Pedido, nuevoEstado: String) {
        registrar(
            pedido.usuarioId,
            "Tu pedido cambió de estado",
            "Pedido " + codigo(pedido.id) + ": " + nuevoEstado
        )
    }

    private suspend fun registrar(usuarioId: Int, titulo: String, mensaje: String) {
        val id = dao.insertar(
            Notificacion(
                usuarioId = usuarioId,
                titulo = titulo,
                mensaje = mensaje,
                fecha = System.currentTimeMillis(),
                destino = "pedidos"
            )
        )
        notificador.mostrar(id.toInt(), titulo, mensaje)
    }

    private fun codigo(id: Int): String = "#" + String.format(Locale.US, "%06d", id)
}