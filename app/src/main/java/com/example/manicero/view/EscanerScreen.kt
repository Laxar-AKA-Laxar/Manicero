package com.example.manicero.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manicero.viewmodel.EscanerViewModel
import com.example.manicero.viewmodel.ResultadoQr
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private fun tienePermisoCamara(contexto: Context): Boolean =
    ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED

@Composable
fun EscanerScreen(
    alAbrirProducto: (Int) -> Unit,
    viewModel: EscanerViewModel = viewModel()
) {
    val contexto = LocalContext.current
    val resultado by viewModel.resultado.collectAsState()
    val manual by viewModel.manual.collectAsState()
    val errorManual by viewModel.errorManual.collectAsState()
    var permitido by remember { mutableStateOf(tienePermisoCamara(contexto)) }
    val solicitar = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permitido = it }

    LaunchedEffect(Unit) {
        if (!permitido) solicitar.launch(Manifest.permission.CAMERA)
    }

    val dueno = contexto.actividad()
    DisposableEffect(dueno) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) permitido = tienePermisoCamara(contexto)
        }
        dueno?.lifecycle?.addObserver(observador)
        onDispose { dueno?.lifecycle?.removeObserver(observador) }
    }

    val colorMarco by animateColorAsState(
        targetValue = when (resultado) {
            is ResultadoQr.Encontrado -> MaterialTheme.colorScheme.primary
            is ResultadoQr.NoEncontrado -> MaterialTheme.colorScheme.error
            is ResultadoQr.Ninguno -> Color.White
        },
        label = "marco"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (permitido) {
                VistaCamara(alLeer = viewModel::procesar, modifier = Modifier.fillMaxSize())
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(220.dp)
                        .border(4.dp, colorMarco, RoundedCornerShape(16.dp))
                )
                Text(
                    text = "Apunta al código QR del producto",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Necesitamos la cámara para leer los códigos QR de los productos",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { solicitar.launch(Manifest.permission.CAMERA) }) {
                        Text("Conceder permiso")
                    }
                    TextButton(
                        onClick = {
                            contexto.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", contexto.packageName, null)
                                )
                            )
                        }
                    ) {
                        Text("Abrir ajustes")
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            AnimatedVisibility(
                visible = resultado !is ResultadoQr.Ninguno,
                enter = fadeIn() + scaleIn(initialScale = 0.9f)
            ) {
                when (val actual = resultado) {
                    is ResultadoQr.Encontrado -> Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Producto encontrado",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = actual.producto.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = precioChileno(actual.producto.precio))
                            Text(text = "Stock: ${actual.producto.stock} un")
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        alAbrirProducto(actual.producto.id)
                                        viewModel.limpiar()
                                    }
                                ) {
                                    Text("Ver ficha")
                                }
                                OutlinedButton(onClick = viewModel::limpiar) {
                                    Text("Escanear otro")
                                }
                            }
                        }
                    }
                    is ResultadoQr.NoEncontrado -> Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Código no reconocido: ${actual.codigo}",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(onClick = viewModel::limpiar) {
                                Text("Intentar de nuevo")
                            }
                        }
                    }
                    is ResultadoQr.Ninguno -> Spacer(modifier = Modifier.height(1.dp))
                }
            }
            if (resultado is ResultadoQr.Ninguno) {
                CampoTexto(
                    valor = manual,
                    alCambiar = viewModel::cambiarManual,
                    etiqueta = "Código manual (ej. OYL-001)",
                    iconoInicial = Icons.Default.Search,
                    error = errorManual,
                    accion = ImeAction.Done
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = viewModel::buscarManual, modifier = Modifier.fillMaxWidth()) {
                    Text("Buscar producto")
                }
            }
        }
    }
}

@Composable
private fun VistaCamara(alLeer: (String) -> Unit, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    val dueno = contexto.actividad() ?: return
    val ejecutor = remember { Executors.newSingleThreadExecutor() }
    val proveedores = remember { mutableListOf<ProcessCameraProvider>() }
    val lector = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_CODE_128
                )
                .build()
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            proveedores.forEach { it.unbindAll() }
            lector.close()
            ejecutor.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val vista = PreviewView(ctx)
            val futuro = ProcessCameraProvider.getInstance(ctx)
            futuro.addListener(
                {
                    val proveedor = futuro.get()
                    proveedores.add(proveedor)
                    val vistaPrevia = Preview.Builder().build().also {
                        it.surfaceProvider = vista.surfaceProvider
                    }
                    val analisis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    analisis.setAnalyzer(ejecutor) { imagen -> analizar(imagen, lector, alLeer) }
                    proveedor.unbindAll()
                    proveedor.bindToLifecycle(
                        dueno,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        vistaPrevia,
                        analisis
                    )
                },
                ContextCompat.getMainExecutor(ctx)
            )
            vista
        }
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun analizar(imagen: ImageProxy, lector: BarcodeScanner, alLeer: (String) -> Unit) {
    val medio = imagen.image
    if (medio == null) {
        imagen.close()
        return
    }
    val entrada = InputImage.fromMediaImage(medio, imagen.imageInfo.rotationDegrees)
    lector.process(entrada)
        .addOnSuccessListener { codigos ->
            codigos.firstOrNull()?.rawValue?.let(alLeer)
        }
        .addOnCompleteListener { imagen.close() }
}