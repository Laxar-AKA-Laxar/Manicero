package com.example.manicero.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manicero.model.Producto
import com.example.manicero.viewmodel.FILTROS_GESTION
import com.example.manicero.viewmodel.GestionViewModel

@Composable
fun GestionProductosScreen(
    alNuevo: () -> Unit,
    alEditar: (Int) -> Unit,
    viewModel: GestionViewModel = viewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    val filtro by viewModel.filtro.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "Gestión de productos", style = MaterialTheme.typography.headlineSmall)
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
                items(FILTROS_GESTION) { opcion ->
                    FilterChip(
                        selected = filtro == opcion,
                        onClick = { viewModel.cambiarFiltro(opcion) },
                        label = { Text(opcion) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(productos, key = { it.id }) { producto ->
                    FilaProducto(
                        producto = producto,
                        alEditar = { alEditar(producto.id) },
                        alCambiarActivo = { activo -> viewModel.cambiarActivo(producto, activo) }
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = alNuevo,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nuevo producto")
        }
    }
}

@Composable
private fun FilaProducto(
    producto: Producto,
    alEditar: () -> Unit,
    alCambiarActivo: (Boolean) -> Unit
) {
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
                Text(text = precioChileno(producto.precio) + " · " + producto.presentacion)
                Text(
                    text = "Stock: ${producto.stock} un",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = if (producto.activo) "Activo" else "Inactivo",
                    color = if (producto.activo) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
            IconButton(onClick = alEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            Switch(checked = producto.activo, onCheckedChange = alCambiarActivo)
        }
    }
}