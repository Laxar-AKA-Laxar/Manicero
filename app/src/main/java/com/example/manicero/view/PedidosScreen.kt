package com.example.manicero.view

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.manicero.model.EstadoPedido
import com.example.manicero.model.Pedido
import com.example.manicero.viewmodel.PedidoViewModel

@Composable
fun PedidosScreen(viewModel: PedidoViewModel) {
    val pedidos by viewModel.pedidos.collectAsState()
    val filtro by viewModel.filtro.collectAsState()
    val esAdmin by viewModel.esAdmin.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (esAdmin) "Pedidos recibidos" else "Mis pedidos",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = filtro == null,
                    onClick = { viewModel.cambiarFiltro(null) },
                    label = { Text("Todos") }
                )
            }
            items(EstadoPedido.todos) { estado ->
                FilterChip(
                    selected = filtro == estado,
                    onClick = { viewModel.cambiarFiltro(estado) },
                    label = { Text(estado) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (pedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay pedidos para mostrar")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pedidos, key = { it.id }) { pedido ->
                    TarjetaPedido(pedido, viewModel, esAdmin)
                }
            }
        }
    }
}

@Composable
private fun TarjetaPedido(pedido: Pedido, viewModel: PedidoViewModel, esAdmin: Boolean) {
    var abierto by remember(pedido.id) { mutableStateOf(false) }
    val detalle by remember(pedido.id) { viewModel.items(pedido.id) }
        .collectAsState(initial = emptyList())
    val siguiente = EstadoPedido.siguiente(pedido.estado)

    Card(
        onClick = { abierto = !abierto },
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = numeroPedido(pedido.id),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = precioChileno(pedido.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(text = fechaPedido(pedido.fecha), style = MaterialTheme.typography.bodySmall)
            Text(
                text = pedido.estado,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
            if (abierto) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                detalle.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${item.cantidad} x ${item.nombre}")
                        Text(text = precioChileno(item.precio * item.cantidad))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Entrega: " + pedido.entrega, style = MaterialTheme.typography.bodySmall)
                if (esAdmin && siguiente != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.avanzar(pedido) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Marcar como $siguiente")
                    }
                }
            }
        }
    }
}