package com.example.manicero.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.manicero.model.Tienda
import com.example.manicero.repository.TiendaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UbicacionUsuario(val latitud: Double, val longitud: Double)

class MapaViewModel : ViewModel() {

    val tiendas: List<Tienda> = TiendaRepository.tiendas

    private val _seleccionada = MutableStateFlow<Tienda?>(null)
    val seleccionada: StateFlow<Tienda?> = _seleccionada

    private val _ubicacion = MutableStateFlow<UbicacionUsuario?>(null)
    val ubicacion: StateFlow<UbicacionUsuario?> = _ubicacion

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso

    fun seleccionar(tienda: Tienda?) {
        _seleccionada.value = tienda
    }

    fun fijarUbicacion(latitud: Double, longitud: Double) {
        _ubicacion.value = UbicacionUsuario(latitud, longitud)
        _aviso.value = null
    }

    fun avisar(texto: String?) {
        _aviso.value = texto
    }

    fun distanciaKm(tienda: Tienda): Double? {
        val usuario = _ubicacion.value ?: return null
        val resultado = FloatArray(1)
        Location.distanceBetween(
            usuario.latitud,
            usuario.longitud,
            tienda.latitud,
            tienda.longitud,
            resultado
        )
        return resultado[0] / 1000.0
    }
}