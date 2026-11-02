package com.example.manicero.model

object EstadoPedido {
    const val PENDIENTE = "Pendiente de Validación"
    const val CONFIRMADO = "Confirmado"
    const val PREPARADO = "Preparado"
    const val ENTREGADO = "Entregado"

    val todos = listOf(PENDIENTE, CONFIRMADO, PREPARADO, ENTREGADO)

    fun siguiente(actual: String): String? {
        val posicion = todos.indexOf(actual)
        return if (posicion < 0) null else todos.getOrNull(posicion + 1)
    }
}