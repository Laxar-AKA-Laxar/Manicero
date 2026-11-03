package com.example.manicero.viewmodel

object ValidadorProducto {

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "El nombre es obligatorio"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }

    fun categoria(valor: String?): String? =
        if (valor.isNullOrBlank()) "Elige una categoría" else null

    fun presentacion(valor: String): String? =
        if (valor.isBlank()) "La presentación es obligatoria (ej. 250 g)" else null

    fun precio(valor: String): String? {
        if (valor.isBlank()) return "El precio es obligatorio"
        val numero = valor.toIntOrNull() ?: return "Ingresa solo números"
        return when {
            numero <= 0 -> "Debe ser mayor a 0"
            numero > 1_000_000 -> "Valor demasiado alto"
            else -> null
        }
    }

    fun stock(valor: String): String? {
        if (valor.isBlank()) return "El stock es obligatorio"
        val numero = valor.toIntOrNull() ?: return "Ingresa solo números"
        return when {
            numero < 0 -> "No puede ser negativo"
            numero > 100_000 -> "Valor demasiado alto"
            else -> null
        }
    }

    fun movimiento(valor: String, tipo: String, stockActual: Int): String? {
        if (valor.isBlank()) return "Ingresa una cantidad"
        val numero = valor.toIntOrNull() ?: return "Ingresa solo números"
        return when {
            numero <= 0 -> "Debe ser mayor a 0"
            numero > 100_000 -> "Cantidad demasiado alta"
            tipo == "Salida" && numero > stockActual -> "No hay stock suficiente (disponible: $stockActual)"
            else -> null
        }
    }
}