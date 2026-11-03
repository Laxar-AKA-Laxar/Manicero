package com.example.manicero.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.manicero.data.AppDatabase
import com.example.manicero.data.Notificador
import com.example.manicero.model.Notificacion
import com.example.manicero.repository.NotificacionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotificacionesViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = NotificacionRepository(
        AppDatabase.obtener(application),
        Notificador(application)
    )

    private val _usuarioId = MutableStateFlow(-1)

    val notificaciones: StateFlow<List<Notificacion>> = _usuarioId
        .flatMapLatest { repositorio.lista(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val noLeidas: StateFlow<Int> = _usuarioId
        .flatMapLatest { repositorio.noLeidas(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun fijarUsuario(id: Int) {
        _usuarioId.value = id
    }

    fun abrir(notificacion: Notificacion) {
        viewModelScope.launch { repositorio.marcarLeida(notificacion.id) }
    }

    fun marcarTodas() {
        viewModelScope.launch { repositorio.marcarTodas(_usuarioId.value) }
    }
}