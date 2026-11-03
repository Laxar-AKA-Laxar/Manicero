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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val FILTROS_GESTION = listOf("Todos", "Activos", "Inactivos")

class GestionViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ProductoRepository(AppDatabase.obtener(application).productoDao())

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    private val _filtro = MutableStateFlow(FILTROS_GESTION.first())
    val filtro: StateFlow<String> = _filtro

    val productos: StateFlow<List<Producto>> = combine(
        repositorio.productos,
        _busqueda,
        _filtro
    ) { lista, texto, filtro ->
        lista.filter { producto ->
            producto.nombre.contains(texto, ignoreCase = true) &&
                    when (filtro) {
                        "Activos" -> producto.activo
                        "Inactivos" -> !producto.activo
                        else -> true
                    }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun cambiarFiltro(filtro: String) {
        _filtro.value = filtro
    }

    fun cambiarActivo(producto: Producto, activo: Boolean) {
        viewModelScope.launch { repositorio.cambiarActivo(producto.id, activo) }
    }
}