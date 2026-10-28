package com.example.manicero.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manicero.model.Producto
import com.example.manicero.viewmodel.ProductoViewModel
import java.util.Locale

private val categorias = listOf("Frutos secos", "Semillas", "Deshidratados")

@Composable
fun CatalogoScreen(viewModel: ProductoViewModel = viewModel()) {
    val productos by viewModel.productos.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    val categoriaElegida by viewModel.categoria.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = viewModel::cambiarBusqueda,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar productos") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = categoriaElegida == null,
                    onClick = { viewModel.cambiarCategoria(null) },
                    label = { Text("Todos") }
                )
            }
            items(categorias) { categoria ->
                FilterChip(
                    selected = categoriaElegida == categoria,
                    onClick = { viewModel.cambiarCategoria(categoria) },
                    label = { Text(categoria) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos, key = { it.id }) { producto ->
                ProductoCard(producto)
            }
        }
    }
}

@Composable
private fun ProductoCard(producto: Producto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "$" + "%,d".format(Locale.forLanguageTag("es-CL"), producto.precio),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Stock: ${producto.stock} un",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}