package com.example.manicero.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.manicero.data.AppDatabase
import com.example.manicero.model.ItemCarritoDetalle
import com.example.manicero.repository.CarritoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = CarritoRepository(AppDatabase.obtener(application).carritoDao())

    private val _usuarioId = MutableStateFlow(-1)

    val items: StateFlow<List<ItemCarritoDetalle>> = _usuarioId
        .flatMapLatest { repositorio.items(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val total: StateFlow<Int> = items
        .map { lista -> lista.sumOf { it.precio * it.cantidad } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cantidadTotal: StateFlow<Int> = items
        .map { lista -> lista.sumOf { it.cantidad } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso

    fun fijarUsuario(id: Int) {
        _usuarioId.value = id
    }

    fun subir(item: ItemCarritoDetalle) {
        if (item.cantidad >= item.stock) {
            mostrarAviso("Stock máximo de ${item.nombre}: ${item.stock} un")
            return
        }
        viewModelScope.launch {
            repositorio.cambiarCantidad(_usuarioId.value, item.productoId, item.cantidad + 1)
        }
    }

    fun bajar(item: ItemCarritoDetalle) {
        if (item.cantidad <= 1) return
        viewModelScope.launch {
            repositorio.cambiarCantidad(_usuarioId.value, item.productoId, item.cantidad - 1)
        }
    }

    fun quitar(item: ItemCarritoDetalle) {
        viewModelScope.launch {
            repositorio.quitar(_usuarioId.value, item.productoId)
        }
    }

    private fun mostrarAviso(texto: String) {
        _aviso.value = texto
        viewModelScope.launch {
            delay(2500)
            _aviso.value = null
        }
    }
}