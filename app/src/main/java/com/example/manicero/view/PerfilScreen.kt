package com.example.manicero.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.manicero.viewmodel.AuthViewModel

@Composable
fun PerfilScreen(viewModel: AuthViewModel, alCerrarSesion: () -> Unit) {
    val usuario by viewModel.usuario.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = usuario?.nombre.orEmpty(),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "@" + usuario?.alias.orEmpty(),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        FilaDato(Icons.Default.Email, usuario?.correo.orEmpty())
        FilaDato(Icons.Default.Phone, usuario?.telefono.orEmpty())
        FilaDato(
            Icons.Default.LocationOn,
            usuario?.direccion.orEmpty().ifBlank { "Sin dirección" }
        )
        FilaDato(
            Icons.Default.Person,
            if (usuario?.rol == "ADMIN") "Administrador" else "Cliente"
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = alCerrarSesion, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun FilaDato(icono: ImageVector, texto: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icono, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = texto, style = MaterialTheme.typography.bodyLarge)
    }
}