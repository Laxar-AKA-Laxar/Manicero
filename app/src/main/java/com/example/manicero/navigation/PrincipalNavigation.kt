package com.example.manicero.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.manicero.view.CarritoScreen
import com.example.manicero.view.CatalogoScreen
import com.example.manicero.view.DetalleScreen
import com.example.manicero.view.PantallaPendiente
import com.example.manicero.view.PerfilScreen
import com.example.manicero.viewmodel.AuthViewModel
import com.example.manicero.viewmodel.CarritoViewModel

private const val RUTA_DETALLE = "detalle/{id}"
private const val RUTA_CARRITO = "carrito"

@Composable
fun PrincipalNavigation(authViewModel: AuthViewModel, alCerrarSesion: () -> Unit) {
    val navController = rememberNavController()
    val carritoViewModel: CarritoViewModel = viewModel()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val usuario by authViewModel.usuario.collectAsState()
    val cantidadCarrito by carritoViewModel.cantidadTotal.collectAsState()

    LaunchedEffect(usuario?.id) {
        carritoViewModel.fijarUsuario(usuario?.id ?: -1)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                rutasBarra.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual == item.ruta,
                        onClick = {
                            navController.navigate(item.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icono, contentDescription = item.titulo) },
                        label = { Text(item.titulo) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Ruta.Inicio.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Ruta.Inicio.ruta) {
                CatalogoScreen(
                    cantidadCarrito = cantidadCarrito,
                    alAbrirProducto = { id -> navController.navigate("detalle/$id") },
                    alAbrirCarrito = { navController.navigate(RUTA_CARRITO) }
                )
            }
            composable(
                route = RUTA_DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                DetalleScreen(
                    usuarioId = usuario?.id ?: -1,
                    alVolver = { navController.popBackStack() },
                    alIrAlCarrito = { navController.navigate(RUTA_CARRITO) }
                )
            }
            composable(RUTA_CARRITO) {
                CarritoScreen(
                    viewModel = carritoViewModel,
                    alVolver = { navController.popBackStack() }
                )
            }
            composable(Ruta.Mapa.ruta) { PantallaPendiente("Mapa de tiendas") }
            composable(Ruta.Escaner.ruta) { PantallaPendiente("Escáner QR") }
            composable(Ruta.Pedidos.ruta) { PantallaPendiente("Mis pedidos") }
            composable(Ruta.Perfil.ruta) { PerfilScreen(authViewModel, alCerrarSesion) }
        }
    }
}