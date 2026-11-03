package com.example.manicero.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.manicero.data.AppDatabase
import com.example.manicero.model.Producto
import com.example.manicero.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormProductoUi(
    val nombre: String = "",
    val categoria: String? = null,
    val precio: String = "",
    val stock: String = "",
    val presentacion: String = "",
    val descripcion: String = "",
    val intento: Boolean = false,
    val cargando: Boolean = false,
    val guardado: Boolean = false,
    val esNuevo: Boolean = true
) {
    private fun visible(valor: String, error: String?): String? =
        if (valor.isNotEmpty() || intento) error else null

    val errNombre: String? get() = visible(nombre, ValidadorProducto.nombre(nombre))

    val errCategoria: String?
        get() = if (intento) ValidadorProducto.categoria(categoria) else null

    val errPrecio: String? get() = visible(precio, ValidadorProducto.precio(precio))

    val errStock: String? get() = visible(stock, ValidadorProducto.stock(stock))

    val errPresentacion: String?
        get() = visible(presentacion, ValidadorProducto.presentacion(presentacion))

    val valido: Boolean
        get() = ValidadorProducto.nombre(nombre) == null &&
                ValidadorProducto.categoria(categoria) == null &&
                ValidadorProducto.precio(precio) == null &&
                ValidadorProducto.stock(stock) == null &&
                ValidadorProducto.presentacion(presentacion) == null
}

class ProductoFormViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repositorio = ProductoRepository(AppDatabase.obtener(application).productoDao())
    private val idProducto: Int = savedStateHandle["id"] ?: -1
    private var original: Producto? = null

    private val _form = MutableStateFlow(FormProductoUi(esNuevo = idProducto == -1))
    val form: StateFlow<FormProductoUi> = _form

    init {
        if (idProducto != -1) {
            viewModelScope.launch {
                repositorio.buscarPorId(idProducto)?.let { producto ->
                    original = producto
                    _form.value = FormProductoUi(
                        nombre = producto.nombre,
                        categoria = producto.categoria,
                        precio = producto.precio.toString(),
                        stock = producto.stock.toString(),
                        presentacion = producto.presentacion,
                        descripcion = producto.descripcion,
                        esNuevo = false
                    )
                }
            }
        }
    }

    fun actualizar(cambio: (FormProductoUi) -> FormProductoUi) {
        _form.update(cambio)
    }

    fun guardar() {
        val actual = _form.value.copy(intento = true)
        _form.value = actual
        if (!actual.valido) return
        viewModelScope.launch {
            _form.update { it.copy(cargando = true) }
            val base = original
            if (base == null) {
                repositorio.guardar(
                    Producto(
                        nombre = actual.nombre.trim(),
                        categoria = actual.categoria.orEmpty(),
                        precio = actual.precio.toInt(),
                        stock = actual.stock.toInt(),
                        codigoQr = repositorio.nuevoCodigo(),
                        descripcion = actual.descripcion.trim(),
                        presentacion = actual.presentacion.trim()
                    )
                )
            } else {
                repositorio.actualizar(
                    base.copy(
                        nombre = actual.nombre.trim(),
                        categoria = actual.categoria.orEmpty(),
                        precio = actual.precio.toInt(),
                        stock = actual.stock.toInt(),
                        descripcion = actual.descripcion.trim(),
                        presentacion = actual.presentacion.trim()
                    )
                )
            }
            _form.update { it.copy(cargando = false, guardado = true) }
        }
    }
}