package com.example.manicero.viewmodel

object Validador {

    private val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val regexTelefono = Regex("^(\\+?56)?9\\d{8}$")

    fun requerido(valor: String, campo: String): String? =
        if (valor.isBlank()) "$campo es obligatorio" else null

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "El nombre es obligatorio"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        valor.any { it.isDigit() } -> "No puede contener números"
        else -> null
    }

    fun alias(valor: String): String? = when {
        valor.isBlank() -> "El alias es obligatorio"
        valor.length < 4 -> "Debe tener al menos 4 caracteres"
        !valor.all { it.isLetterOrDigit() || it == '_' } -> "Solo letras, números y guion bajo"
        else -> null
    }

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "El correo es obligatorio"
        !regexCorreo.matches(valor.trim()) -> "Ingresa un correo válido"
        else -> null
    }

    fun telefono(valor: String): String? {
        if (valor.isBlank()) return "El teléfono es obligatorio"
        val limpio = valor.replace(" ", "")
        return if (regexTelefono.matches(limpio)) null else "Formato válido: +56 9 1234 5678"
    }

    fun clave(valor: String): String? = when {
        valor.isBlank() -> "La contraseña es obligatoria"
        valor.length < 6 -> "Debe tener al menos 6 caracteres"
        valor.none { it.isLetter() } || valor.none { it.isDigit() } -> "Debe incluir letras y números"
        else -> null
    }

    fun confirmacion(clave: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Repite la contraseña"
        clave != confirmacion -> "Las contraseñas no coinciden"
        else -> null
    }
}