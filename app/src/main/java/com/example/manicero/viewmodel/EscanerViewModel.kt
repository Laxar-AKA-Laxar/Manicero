package com.example.manicero.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.manicero.data.AppDatabase
import com.example.manicero.model.Producto
import com.example.manicero.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ResultadoQr {
    data object Ninguno : ResultadoQr
    data class Encontrado(val producto: Producto) : ResultadoQr
    data class NoEncontrado(val codigo: String) : ResultadoQr
}

class EscanerViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ProductoRepository(AppDatabase.obtener(application).productoDao())

    private val _resultado = MutableStateFlow<ResultadoQr>(ResultadoQr.Ninguno)
    val resultado: StateFlow<ResultadoQr> = _resultado

    private val _manual = MutableStateFlow("")
    val manual: StateFlow<String> = _manual

    private val _errorManual = MutableStateFlow<String?>(null)
    val errorManual: StateFlow<String?> = _errorManual

    private var buscando = false

    fun cambiarManual(valor: String) {
        _manual.value = valor
        _errorManual.value = null
    }

    fun procesar(codigo: String) {
        val limpio = codigo.trim()
        if (limpio.isEmpty() || buscando || _resultado.value !is ResultadoQr.Ninguno) return
        buscando = true
        viewModelScope.launch {
            val producto = repositorio.buscarPorQr(limpio)
            _resultado.value = if (producto != null) {
                ResultadoQr.Encontrado(producto)
            } else {
                ResultadoQr.NoEncontrado(limpio)
            }
            buscando = false
        }
    }

    fun buscarManual() {
        val error = Validador.requerido(_manual.value, "El código")
        _errorManual.value = error
        if (error == null) procesar(_manual.value)
    }

    fun limpiar() {
        _resultado.value = ResultadoQr.Ninguno
        _manual.value = ""
        _errorManual.value = null
    }
}