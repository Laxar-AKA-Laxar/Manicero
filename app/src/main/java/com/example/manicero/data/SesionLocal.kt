package com.example.manicero.data

import android.content.Context

class SesionLocal(context: Context) {

    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    fun guardar(usuarioId: Int) {
        prefs.edit().putInt("usuario_id", usuarioId).apply()
    }

    fun obtener(): Int = prefs.getInt("usuario_id", -1)

    fun cerrar() {
        prefs.edit().remove("usuario_id").apply()
    }
}