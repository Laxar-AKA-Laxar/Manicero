package com.example.manicero.repository

import com.example.manicero.data.Seguridad
import com.example.manicero.data.UsuarioDao
import com.example.manicero.model.Usuario

class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun registrar(usuario: Usuario): Usuario? {
        if (dao.buscarPorAlias(usuario.alias) != null) return null
        val id = dao.insertar(usuario)
        return usuario.copy(id = id.toInt())
    }

    suspend fun autenticar(alias: String, claveHash: String): Usuario? {
        val usuario = dao.buscarPorAlias(alias) ?: return null
        return if (usuario.claveHash == claveHash) usuario else null
    }

    suspend fun buscarPorId(id: Int): Usuario? = dao.buscarPorId(id)

    suspend fun asegurarAdministrador() {
        if (dao.buscarPorAlias("admin") == null) {
            dao.insertar(
                Usuario(
                    nombre = "Administrador",
                    alias = "admin",
                    correo = "admin@admin.cl",
                    telefono = "+56 9 1234 1234",
                    direccion = "Santiago",
                    claveHash = Seguridad.hash("admin123"),
                    rol = "ADMIN"
                )
            )
        }
    }
}