package com.example.manicero.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manicero.model.Tienda
import com.example.manicero.viewmodel.MapaViewModel
import com.example.manicero.viewmodel.UbicacionUsuario
import java.io.File
import java.util.Locale
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private fun configurarMapa(contexto: Context) {
    val configuracion = Configuration.getInstance()
    configuracion.load(contexto, contexto.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    configuracion.userAgentValue = contexto.packageName
    configuracion.osmdroidBasePath = File(contexto.cacheDir, "osmdroid")
    configuracion.osmdroidTileCache = File(contexto.cacheDir, "osmdroid/tiles")
}

private fun tienePermisoUbicacion(contexto: Context): Boolean =
    ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

@SuppressLint("MissingPermission")
private fun pedirUbicacion(contexto: Context, alObtener: (Location?) -> Unit) {
    val administrador = contexto.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val precisa = ContextCompat.checkSelfPermission(
        contexto,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val candidatos = if (precisa) {
        listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
    } else {
        listOf(LocationManager.NETWORK_PROVIDER)
    }
    val proveedor = candidatos.firstOrNull { administrador.isProviderEnabled(it) }
    if (proveedor == null) {
        alObtener(null)
        return
    }
    try {
        LocationManagerCompat.getCurrentLocation(
            administrador,
            proveedor,
            null,
            ContextCompat.getMainExecutor(contexto)
        ) { alObtener(it) }
    } catch (e: SecurityException) {
        alObtener(null)
    }
}

private fun repintar(
    mapa: MapView,
    tiendas: List<Tienda>,
    ubicacion: UbicacionUsuario?,
    alTocar: (Tienda) -> Unit
) {
    mapa.overlays.clear()
    tiendas.forEach { tienda ->
        val marcador = Marker(mapa)
        marcador.position = GeoPoint(tienda.latitud, tienda.longitud)
        marcador.title = tienda.nombre
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marcador.setOnMarkerClickListener { _, _ ->
            alTocar(tienda)
            true
        }
        mapa.overlays.add(marcador)
    }
    if (ubicacion != null) {
        val propio = Marker(mapa)
        propio.position = GeoPoint(ubicacion.latitud, ubicacion.longitud)
        propio.title = "Tu ubicación"
        propio.icon = ContextCompat.getDrawable(mapa.context, android.R.drawable.ic_menu_mylocation)
        propio.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        mapa.overlays.add(propio)
    }
    mapa.invalidate()
}

@Composable
fun MapaScreen(viewModel: MapaViewModel = viewModel()) {
    val contexto = LocalContext.current
    val seleccionada by viewModel.seleccionada.collectAsState()
    val ubicacion by viewModel.ubicacion.collectAsState()
    val aviso by viewModel.aviso.collectAsState()

    val mapa = remember {
        configurarMapa(contexto)
        MapView(contexto).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(-33.4489, -70.6693))
        }
    }

    val dueno = contexto.actividad()
    DisposableEffect(dueno) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) mapa.onResume()
            if (evento == Lifecycle.Event.ON_PAUSE) mapa.onPause()
        }
        dueno?.lifecycle?.addObserver(observador)
        onDispose {
            dueno?.lifecycle?.removeObserver(observador)
            mapa.onDetach()
        }
    }

    LaunchedEffect(seleccionada) {
        seleccionada?.let { mapa.controller.animateTo(GeoPoint(it.latitud, it.longitud)) }
    }

    LaunchedEffect(ubicacion) {
        ubicacion?.let {
            mapa.controller.setZoom(14.0)
            mapa.controller.animateTo(GeoPoint(it.latitud, it.longitud))
        }
    }

    val buscarUbicacion = {
        pedirUbicacion(contexto) { encontrada ->
            if (encontrada != null) {
                viewModel.fijarUbicacion(encontrada.latitude, encontrada.longitude)
            } else {
                viewModel.avisar("No se pudo obtener tu ubicación. Activa el GPS.")
            }
        }
    }

    val solicitar = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos.values.any { it }) {
            buscarUbicacion()
        } else {
            viewModel.avisar("Permiso de ubicación denegado. Puedes activarlo en Ajustes.")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapa },
            update = { vista ->
                repintar(vista, viewModel.tiendas, ubicacion) { viewModel.seleccionar(it) }
            }
        )
        AnimatedVisibility(
            visible = aviso != null,
            modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = aviso.orEmpty(),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        FloatingActionButton(
            onClick = {
                if (tienePermisoUbicacion(contexto)) {
                    buscarUbicacion()
                } else {
                    solicitar.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Mi ubicación")
        }
        AnimatedVisibility(
            visible = seleccionada != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it } + fadeIn()
        ) {
            seleccionada?.let { tienda ->
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = tienda.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tienda.tipo,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(text = tienda.direccion)
                        Text(text = tienda.horario)
                        viewModel.distanciaKm(tienda)?.let { distancia ->
                            Text(
                                text = String.format(
                                    Locale.forLanguageTag("es-CL"),
                                    "A %.1f km de ti",
                                    distancia
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val destino = Uri.parse(
                                        "geo:${tienda.latitud},${tienda.longitud}" +
                                                "?q=${tienda.latitud},${tienda.longitud}" +
                                                "(${Uri.encode(tienda.nombre)})"
                                    )
                                    try {
                                        contexto.startActivity(Intent(Intent.ACTION_VIEW, destino))
                                    } catch (e: ActivityNotFoundException) {
                                        viewModel.avisar("No hay una aplicación de mapas instalada")
                                    }
                                }
                            ) {
                                Text("Cómo llegar")
                            }
                            OutlinedButton(onClick = { viewModel.seleccionar(null) }) {
                                Text("Cerrar")
                            }
                        }
                    }
                }
            }
        }
    }
}