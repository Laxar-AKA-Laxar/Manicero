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

class ProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductoRepository(
        AppDatabase.obtener(application).productoDao()
    )

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    private val _categoria = MutableStateFlow<String?>(null)
    val categoria: StateFlow<String?> = _categoria

    val productos: StateFlow<List<Producto>> = combine(
        repository.productos,
        _busqueda,
        _categoria
    ) { lista, texto, categoriaElegida ->
        lista.filter { producto ->
            producto.nombre.contains(texto, ignoreCase = true) &&
                    (categoriaElegida == null || producto.categoria == categoriaElegida)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.cargarDatosDePrueba()
        }
    }

    fun cambiarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun cambiarCategoria(nueva: String?) {
        _categoria.value = nueva
    }
}