package com.example.manicero.data

import java.security.MessageDigest

object Seguridad {

    fun hash(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
}