package com.example.manicero.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.manicero.data.AppDatabase
import com.example.manicero.model.Producto
import com.example.manicero.repository.CarritoRepository
import com.example.manicero.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MensajeUi(val texto: String, val exito: Boolean)

class DetalleViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val base = AppDatabase.obtener(application)
    private val productos = ProductoRepository(base.productoDao())
    private val carrito = CarritoRepository(base.carritoDao())
    private val idProducto: Int = savedStateHandle["id"] ?: -1

    val producto: StateFlow<Producto?> = productos.observarPorId(idProducto)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _cantidad = MutableStateFlow(1)
    val cantidad: StateFlow<Int> = _cantidad

    private val _mensaje = MutableStateFlow<MensajeUi?>(null)
    val mensaje: StateFlow<MensajeUi?> = _mensaje

    fun subir() {
        val stock = producto.value?.stock ?: return
        if (_cantidad.value < stock) _cantidad.value += 1
        _mensaje.value = null
    }

    fun bajar() {
        if (_cantidad.value > 1) _cantidad.value -= 1
        _mensaje.value = null
    }

    fun agregar(usuarioId: Int) {
        val actual = producto.value ?: return
        viewModelScope.launch {
            val agregado = carrito.agregar(usuarioId, actual, _cantidad.value)
            if (agregado) {
                _mensaje.value = MensajeUi("Agregado al carrito", true)
                _cantidad.value = 1
            } else {
                _mensaje.value = MensajeUi("No hay stock suficiente para esa cantidad", false)
            }
        }
    }
}