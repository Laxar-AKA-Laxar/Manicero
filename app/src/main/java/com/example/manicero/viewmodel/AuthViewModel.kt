package com.example.manicero.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.manicero.data.AppDatabase
import com.example.manicero.data.Seguridad
import com.example.manicero.data.SesionLocal
import com.example.manicero.model.Usuario
import com.example.manicero.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUi(
    val alias: String = "",
    val clave: String = "",
    val intento: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null
) {
    val errAlias: String?
        get() = if (alias.isNotEmpty() || intento) Validador.requerido(alias, "El alias") else null

    val errClave: String?
        get() = if (clave.isNotEmpty() || intento) Validador.requerido(clave, "La contraseña") else null

    val valido: Boolean
        get() = Validador.requerido(alias, "El alias") == null &&
                Validador.requerido(clave, "La contraseña") == null
}

data class RegistroUi(
    val nombre: String = "",
    val alias: String = "",
    val correo: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val clave: String = "",
    val confirmacion: String = "",
    val aliasEnUso: String? = null,
    val intento: Boolean = false,
    val cargando: Boolean = false
) {
    private fun visible(valor: String, error: String?): String? =
        if (valor.isNotEmpty() || intento) error else null

    val errNombre: String? get() = visible(nombre, Validador.nombre(nombre))

    val errAlias: String?
        get() = if (alias.trim().lowercase() == aliasEnUso) {
            "Ese alias ya está en uso"
        } else {
            visible(alias, Validador.alias(alias))
        }

    val errCorreo: String? get() = visible(correo, Validador.correo(correo))

    val errTelefono: String? get() = visible(telefono, Validador.telefono(telefono))

    val errClave: String? get() = visible(clave, Validador.clave(clave))

    val errConfirmacion: String?
        get() = visible(confirmacion, Validador.confirmacion(clave, confirmacion))

    val valido: Boolean
        get() = Validador.nombre(nombre) == null &&
                Validador.alias(alias) == null &&
                alias.trim().lowercase() != aliasEnUso &&
                Validador.correo(correo) == null &&
                Validador.telefono(telefono) == null &&
                Validador.clave(clave) == null &&
                Validador.confirmacion(clave, confirmacion) == null
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepository(AppDatabase.obtener(application).usuarioDao())
    private val sesion = SesionLocal(application)

    private val _login = MutableStateFlow(LoginUi())
    val login: StateFlow<LoginUi> = _login

    private val _registro = MutableStateFlow(RegistroUi())
    val registro: StateFlow<RegistroUi> = _registro

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _listo = MutableStateFlow(false)
    val listo: StateFlow<Boolean> = _listo

    init {
        viewModelScope.launch {
            repositorio.asegurarAdministrador()
            val id = sesion.obtener()
            if (id != -1) _usuario.value = repositorio.buscarPorId(id)
            _listo.value = true
        }
    }

    fun cambiarLoginAlias(valor: String) {
        _login.update { it.copy(alias = valor, error = null) }
    }

    fun cambiarLoginClave(valor: String) {
        _login.update { it.copy(clave = valor, error = null) }
    }

    fun actualizarRegistro(cambio: (RegistroUi) -> RegistroUi) {
        _registro.update(cambio)
    }

    fun iniciarSesion(alAcertar: () -> Unit) {
        val actual = _login.value.copy(intento = true)
        _login.value = actual
        if (!actual.valido) return
        viewModelScope.launch {
            _login.update { it.copy(cargando = true, error = null) }
            val encontrado = repositorio.autenticar(
                actual.alias.trim().lowercase(),
                Seguridad.hash(actual.clave)
            )
            if (encontrado == null) {
                _login.update { it.copy(cargando = false, error = "Alias o contraseña incorrectos") }
            } else {
                sesion.guardar(encontrado.id)
                _usuario.value = encontrado
                _login.value = LoginUi()
                alAcertar()
            }
        }
    }

    fun registrar(alAcertar: () -> Unit) {
        val actual = _registro.value.copy(intento = true)
        _registro.value = actual
        if (!actual.valido) return
        viewModelScope.launch {
            _registro.update { it.copy(cargando = true) }
            val nuevo = Usuario(
                nombre = actual.nombre.trim(),
                alias = actual.alias.trim().lowercase(),
                correo = actual.correo.trim(),
                telefono = actual.telefono.trim(),
                direccion = actual.direccion.trim(),
                claveHash = Seguridad.hash(actual.clave),
                rol = "CLIENTE"
            )
            val creado = repositorio.registrar(nuevo)
            if (creado == null) {
                _registro.update { it.copy(cargando = false, aliasEnUso = nuevo.alias) }
            } else {
                sesion.guardar(creado.id)
                _usuario.value = creado
                _registro.value = RegistroUi()
                alAcertar()
            }
        }
    }

    fun cerrarSesion() {
        sesion.cerrar()
        _usuario.value = null
    }
}