package com.example.manicero.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manicero.viewmodel.DetalleViewModel

@Composable
fun DetalleScreen(
    usuarioId: Int,
    alVolver: () -> Unit,
    alIrAlCarrito: () -> Unit,
    viewModel: DetalleViewModel = viewModel()
) {
    val producto by viewModel.producto.collectAsState()
    val cantidad by viewModel.cantidad.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val actual = producto
    val aviso = mensaje

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        IconButton(onClick = alVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
        if (actual == null) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Text(
                text = actual.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = actual.categoria, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = precioChileno(actual.precio),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (actual.stock > 0) "En stock: ${actual.stock} un" else "Agotado",
                color = if (actual.stock > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilledTonalIconButton(onClick = viewModel::bajar, enabled = cantidad > 1) {
                    Text("−")
                }
                AnimatedContent(targetState = cantidad, label = "cantidad") { valor ->
                    Text(text = valor.toString(), style = MaterialTheme.typography.titleLarge)
                }
                FilledTonalIconButton(
                    onClick = viewModel::subir,
                    enabled = cantidad < actual.stock
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Subtotal: " + precioChileno(actual.precio * cantidad),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.agregar(usuarioId) },
                enabled = actual.stock > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar al carrito")
            }
            AnimatedVisibility(visible = aviso != null) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = aviso?.texto.orEmpty(),
                        color = if (aviso?.exito == true) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                    if (aviso?.exito == true) {
                        OutlinedButton(
                            onClick = alIrAlCarrito,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver carrito")
                        }
                    }
                }
            }
        }
    }
}