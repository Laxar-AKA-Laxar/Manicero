package com.example.manicero.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manicero.viewmodel.ProductoFormViewModel
import kotlinx.coroutines.delay

private val categoriasProducto = listOf("Frutos secos", "Semillas", "Deshidratados")

@Composable
fun ProductoFormScreen(
    alVolver: () -> Unit,
    viewModel: ProductoFormViewModel = viewModel()
) {
    val estado by viewModel.form.collectAsState()

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) {
            delay(1200)
            alVolver()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = alVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = if (estado.esNuevo) "Nuevo producto" else "Editar producto",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        CampoTexto(
            valor = estado.nombre,
            alCambiar = { valor -> viewModel.actualizar { it.copy(nombre = valor) } },
            etiqueta = "Nombre del producto",
            iconoInicial = Icons.Default.Edit,
            error = estado.errNombre
        )
        Column {
            Text(text = "Categoría", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categoriasProducto.forEach { categoria ->
                    FilterChip(
                        selected = estado.categoria == categoria,
                        onClick = { viewModel.actualizar { it.copy(categoria = categoria) } },
                        label = { Text(categoria) }
                    )
                }
            }
            AnimatedVisibility(visible = estado.errCategoria != null) {
                Text(
                    text = estado.errCategoria.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        CampoTexto(
            valor = estado.presentacion,
            alCambiar = { valor -> viewModel.actualizar { it.copy(presentacion = valor) } },
            etiqueta = "Presentación (ej. 250 g)",
            iconoInicial = Icons.Default.Info,
            error = estado.errPresentacion
        )
        CampoTexto(
            valor = estado.precio,
            alCambiar = { valor -> viewModel.actualizar { it.copy(precio = valor) } },
            etiqueta = "Precio en pesos",
            iconoInicial = Icons.Default.ShoppingCart,
            error = estado.errPrecio,
            teclado = KeyboardType.Number
        )
        CampoTexto(
            valor = estado.stock,
            alCambiar = { valor -> viewModel.actualizar { it.copy(stock = valor) } },
            etiqueta = "Stock disponible",
            iconoInicial = Icons.Default.Add,
            error = estado.errStock,
            teclado = KeyboardType.Number
        )
        CampoTexto(
            valor = estado.descripcion,
            alCambiar = { valor -> viewModel.actualizar { it.copy(descripcion = valor) } },
            etiqueta = "Descripción (opcional)",
            iconoInicial = Icons.Default.Create,
            error = null,
            accion = ImeAction.Done
        )
        Button(
            onClick = viewModel::guardar,
            enabled = !estado.cargando && !estado.guardado,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(if (estado.esNuevo) "Crear producto" else "Guardar cambios")
            }
        }
        AnimatedVisibility(
            visible = estado.guardado,
            enter = scaleIn(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                initialScale = 0.3f
            ) + fadeIn()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = "Producto guardado", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}