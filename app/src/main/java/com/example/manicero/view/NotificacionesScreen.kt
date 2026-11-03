package com.example.manicero.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.manicero.model.Notificacion
import com.example.manicero.viewmodel.NotificacionesViewModel

private fun tienePermisoAvisos(contexto: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

@Composable
fun NotificacionesScreen(
    viewModel: NotificacionesViewModel,
    alAbrir: (String) -> Unit,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val lista by viewModel.notificaciones.collectAsState()
    var permitido by remember { mutableStateOf(tienePermisoAvisos(contexto)) }
    val solicitar = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permitido = it }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = alVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Notificaciones",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = viewModel::marcarTodas) { Text("Marcar leídas") }
        }
        if (!permitido) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Los avisos del teléfono están desactivados")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { solicitar.launch(Manifest.permission.POST_NOTIFICATIONS) }
                    ) {
                        Text("Activar avisos")
                    }
                }
            }
        }
        if (lista.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes notificaciones")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lista, key = { it.id }) { notificacion ->
                    TarjetaNotificacion(notificacion) {
                        viewModel.abrir(notificacion)
                        alAbrir(notificacion.destino)
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaNotificacion(notificacion: Notificacion, alTocar: () -> Unit) {
    Card(
        onClick = alTocar,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = notificacion.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold
            )
            Text(text = notificacion.mensaje)
            Text(
                text = fechaPedido(notificacion.fecha),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}