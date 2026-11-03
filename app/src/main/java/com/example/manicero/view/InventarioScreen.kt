package com.example.manicero.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manicero.model.Producto
import com.example.manicero.viewmodel.FILTROS_INVENTARIO
import com.example.manicero.viewmodel.InventarioViewModel

@Composable
fun InventarioScreen(viewModel: InventarioViewModel = viewModel()) {
    val productos by viewModel.productos.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    val filtro by viewModel.filtro.collectAsState()
    val movimiento by viewModel.movimiento.collectAsState()
    val cantidadBajos by viewModel.cantidadBajos.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Inventario", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = if (cantidadBajos == 0) {
                "Sin productos con stock bajo"
            } else {
                "$cantidadBajos productos con stock bajo"
            },
            color = if (cantidadBajos == 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = busqueda,
            onValueChange = viewModel::cambiarBusqueda,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar producto") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(FILTROS_INVENTARIO) { opcion ->
                FilterChip(
                    selected = filtro == opcion,
                    onClick = { viewModel.cambiarFiltro(opcion) },
                    label = { Text(opcion) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(productos, key = { it.id }) { producto ->
                FilaInventario(producto) { viewModel.abrirMovimiento(producto) }
            }
        }
    }

    movimiento?.let { actual ->
        AlertDialog(
            onDismissRequest = viewModel::cerrarMovimiento,
            title = { Text("Movimiento: ${actual.producto.nombre}") },
            text = {
                Column {
                    Text(text = "Stock actual: ${actual.producto.stock} un")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Entrada", "Salida").forEach { tipo ->
                            FilterChip(
                                selected = actual.tipo == tipo,
                                onClick = { viewModel.cambiarTipo(tipo) },
                                label = { Text(tipo) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    CampoTexto(
                        valor = actual.cantidad,
                        alCambiar = viewModel::cambiarCantidad,
                        etiqueta = "Cantidad",
                        iconoInicial = Icons.Default.Search,
                        error = actual.error,
                        teclado = KeyboardType.Number,
                        accion = ImeAction.Done
                    )
                }
            },
            confirmButton = {
                Button(onClick = viewModel::confirmarMovimiento) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cerrarMovimiento) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FilaInventario(producto: Producto, alRegistrar: () -> Unit) {
    val bajo = producto.stock <= producto.stockMinimo
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Stock actual: ${producto.stock} · Mínimo: ${producto.stockMinimo}")
                if (bajo) {
                    Text(
                        text = "Stock bajo",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                if (!producto.activo) {
                    Text(text = "Inactivo", style = MaterialTheme.typography.bodySmall)
                }
            }
            OutlinedButton(onClick = alRegistrar) { Text("Movimiento") }
        }
    }
}