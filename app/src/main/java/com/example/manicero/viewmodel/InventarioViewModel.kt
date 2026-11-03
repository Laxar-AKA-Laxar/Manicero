package com.example.manicero.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.manicero.data.AppDatabase
import com.example.manicero.model.Producto
import com.example.manicero.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val FILTROS_INVENTARIO = listOf("Todos", "Stock bajo")

data class MovimientoUi(
    val producto: Producto,
    val tipo: String = "Entrada",
    val cantidad: String = "",
    val intento: Boolean = false
) {
    val error: String?
        get() = if (cantidad.isNotEmpty() || intento) {
            ValidadorProducto.movimiento(cantidad, tipo, producto.stock)
        } else {
            null
        }
}

class InventarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ProductoRepository(AppDatabase.obtener(application).productoDao())

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    private val _filtro = MutableStateFlow(FILTROS_INVENTARIO.first())
    val filtro: StateFlow<String> = _filtro

    private val _movimiento = MutableStateFlow<MovimientoUi?>(null)
    val movimiento: StateFlow<MovimientoUi?> = _movimiento

    val productos: StateFlow<List<Producto>> = combine(
        repositorio.productos,
        _busqueda,
        _filtro
    ) { lista, texto, filtro ->
        lista.filter { producto ->
            producto.nombre.contains(texto, ignoreCase = true) &&
                    (filtro != "Stock bajo" || producto.stock <= producto.stockMinimo)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cantidadBajos: StateFlow<Int> = repositorio.productos
        .map { lista -> lista.count { it.activo && it.stock <= it.stockMinimo } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun cambiarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun cambiarFiltro(filtro: String) {
        _filtro.value = filtro
    }

    fun abrirMovimiento(producto: Producto) {
        _movimiento.value = MovimientoUi(producto)
    }

    fun cerrarMovimiento() {
        _movimiento.value = null
    }

    fun cambiarTipo(tipo: String) {
        _movimiento.update { it?.copy(tipo = tipo) }
    }

    fun cambiarCantidad(cantidad: String) {
        _movimiento.update { it?.copy(cantidad = cantidad) }
    }

    fun confirmarMovimiento() {
        val actual = _movimiento.value?.copy(intento = true) ?: return
        _movimiento.value = actual
        if (actual.error != null) return
        val cantidad = actual.cantidad.toInt()
        val cambio = if (actual.tipo == "Entrada") cantidad else -cantidad
        viewModelScope.launch {
            repositorio.ajustarStock(actual.producto.id, cambio)
            _movimiento.value = null
        }
    }
}