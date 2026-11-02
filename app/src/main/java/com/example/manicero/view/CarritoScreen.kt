package com.example.manicero.view

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.manicero.model.ItemCarritoDetalle
import com.example.manicero.viewmodel.CarritoViewModel

@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel,
    alVolver: () -> Unit,
    alContinuar: () -> Unit
) {
    val lista by viewModel.items.collectAsState()
    val total by viewModel.total.collectAsState()
    val aviso by viewModel.aviso.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = alVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(text = "Mi carrito", style = MaterialTheme.typography.headlineSmall)
        }
        AnimatedVisibility(visible = aviso != null) {
            Text(
                text = aviso.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        if (lista.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Tu carrito está vacío")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lista, key = { it.productoId }) { item ->
                    FilaCarrito(item, viewModel)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = precioChileno(total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = alContinuar, modifier = Modifier.fillMaxWidth()) {
                Text("Continuar compra")
            }
        }
    }
}
@Composable
private fun LazyItemScope.FilaCarrito(item: ItemCarritoDetalle, viewModel: CarritoViewModel) {
    Card(modifier = Modifier.fillMaxWidth().animateItem()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = item.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = precioChileno(item.precio) + " c/u",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = { viewModel.quitar(item) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Quitar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = { viewModel.bajar(item) },
                        enabled = item.cantidad > 1
                    ) {
                        Text("−")
                    }
                    Text(text = item.cantidad.toString(), style = MaterialTheme.typography.titleMedium)
                    FilledTonalIconButton(onClick = { viewModel.subir(item) }) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar")
                    }
                }
                Text(
                    text = precioChileno(item.precio * item.cantidad),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}