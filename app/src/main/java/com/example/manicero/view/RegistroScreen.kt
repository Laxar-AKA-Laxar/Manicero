package com.example.manicero.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.manicero.viewmodel.AuthViewModel

@Composable
fun RegistroScreen(
    viewModel: AuthViewModel,
    alRegistrar: () -> Unit,
    alIrALogin: () -> Unit
) {
    val estado by viewModel.registro.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        CampoTexto(
            valor = estado.nombre,
            alCambiar = { valor -> viewModel.actualizarRegistro { it.copy(nombre = valor) } },
            etiqueta = "Nombre completo",
            iconoInicial = Icons.Default.Person,
            error = estado.errNombre
        )
        CampoTexto(
            valor = estado.alias,
            alCambiar = { valor -> viewModel.actualizarRegistro { it.copy(alias = valor) } },
            etiqueta = "Alias",
            iconoInicial = Icons.Default.Face,
            error = estado.errAlias
        )
        CampoTexto(
            valor = estado.correo,
            alCambiar = { valor -> viewModel.actualizarRegistro { it.copy(correo = valor) } },
            etiqueta = "Correo electrónico",
            iconoInicial = Icons.Default.Email,
            error = estado.errCorreo,
            teclado = KeyboardType.Email
        )
        CampoTexto(
            valor = estado.telefono,
            alCambiar = { valor -> viewModel.actualizarRegistro { it.copy(telefono = valor) } },
            etiqueta = "Teléfono",
            iconoInicial = Icons.Default.Phone,
            error = estado.errTelefono,
            teclado = KeyboardType.Phone
        )
        CampoTexto(
            valor = estado.direccion,
            alCambiar = { valor -> viewModel.actualizarRegistro { it.copy(direccion = valor) } },
            etiqueta = "Dirección (opcional)",
            iconoInicial = Icons.Default.LocationOn,
            error = null
        )
        CampoTexto(
            valor = estado.clave,
            alCambiar = { valor -> viewModel.actualizarRegistro { it.copy(clave = valor) } },
            etiqueta = "Contraseña",
            iconoInicial = Icons.Default.Lock,
            error = estado.errClave,
            esClave = true
        )
        CampoTexto(
            valor = estado.confirmacion,
            alCambiar = { valor -> viewModel.actualizarRegistro { it.copy(confirmacion = valor) } },
            etiqueta = "Repetir contraseña",
            iconoInicial = Icons.Default.Lock,
            error = estado.errConfirmacion,
            esClave = true,
            accion = ImeAction.Done
        )
        Button(
            onClick = { viewModel.registrar(alRegistrar) },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Crear cuenta")
            }
        }
        TextButton(onClick = alIrALogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}