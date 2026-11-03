package com.example.manicero.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.manicero.data.AppDatabase
import com.example.manicero.data.Notificador
import com.example.manicero.model.EstadoPedido
import com.example.manicero.model.Pedido
import com.example.manicero.model.PedidoItem
import com.example.manicero.model.Usuario
import com.example.manicero.repository.NotificacionRepository
import com.example.manicero.repository.PedidoRepository
import com.example.manicero.repository.StockInsuficienteException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConfirmarUi(
    val entrega: String = "Retiro en tienda",
    val cargando: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = PedidoRepository(AppDatabase.obtener(application))
    private val notificaciones = NotificacionRepository(
        AppDatabase.obtener(application),
        Notificador(application)
    )

    private val _usuario = MutableStateFlow<Usuario?>(null)

    private val _filtro = MutableStateFlow<String?>(null)
    val filtro: StateFlow<String?> = _filtro

    private val _confirmar = MutableStateFlow(ConfirmarUi())
    val confirmar: StateFlow<ConfirmarUi> = _confirmar

    val esAdmin: StateFlow<Boolean> = _usuario
        .map { it?.rol == "ADMIN" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val todos: Flow<List<Pedido>> = _usuario.flatMapLatest { usuario ->
        when {
            usuario == null -> flowOf(emptyList())
            usuario.rol == "ADMIN" -> repositorio.todos()
            else -> repositorio.delUsuario(usuario.id)
        }
    }

    val pedidos: StateFlow<List<Pedido>> = combine(todos, _filtro) { lista, estado ->
        if (estado == null) lista else lista.filter { it.estado == estado }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fijarUsuario(usuario: Usuario?) {
        _usuario.value = usuario
    }

    fun cambiarFiltro(estado: String?) {
        _filtro.value = estado
    }

    fun cambiarEntrega(entrega: String) {
        _confirmar.update { it.copy(entrega = entrega, error = null) }
    }

    fun items(pedidoId: Int): Flow<List<PedidoItem>> = repositorio.items(pedidoId)

    fun avanzar(pedido: Pedido) {
        val siguiente = EstadoPedido.siguiente(pedido.estado) ?: return
        viewModelScope.launch {
            repositorio.cambiarEstado(pedido.id, siguiente)
            notificaciones.estadoCambiado(pedido, siguiente)
        }
    }

    fun confirmarPedido(alExito: (Int) -> Unit) {
        val usuario = _usuario.value ?: return
        viewModelScope.launch {
            _confirmar.update { it.copy(cargando = true, error = null) }
            try {
                val id = repositorio.confirmar(usuario.id, _confirmar.value.entrega)
                notificaciones.nuevoPedido(id.toInt())
                _confirmar.value = ConfirmarUi()
                alExito(id.toInt())
            } catch (e: StockInsuficienteException) {
                _confirmar.update {
                    it.copy(
                        cargando = false,
                        error = "Stock insuficiente de ${e.producto}. Ajusta tu carrito."
                    )
                }
            } catch (e: IllegalStateException) {
                _confirmar.update { it.copy(cargando = false, error = "Tu carrito está vacío") }
            }
        }
    }
}