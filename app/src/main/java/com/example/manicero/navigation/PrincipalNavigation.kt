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
import com.example.manicero.view.ConfirmarPedidoScreen
import com.example.manicero.view.DetalleScreen
import com.example.manicero.view.EscanerScreen
import com.example.manicero.view.ExitoPedidoScreen
import com.example.manicero.view.GestionProductosScreen
import com.example.manicero.view.InventarioScreen
import com.example.manicero.view.MapaScreen
import com.example.manicero.view.PedidosScreen
import com.example.manicero.view.PerfilScreen
import com.example.manicero.view.ProductoFormScreen
import com.example.manicero.viewmodel.AuthViewModel
import com.example.manicero.viewmodel.CarritoViewModel
import com.example.manicero.viewmodel.PedidoViewModel

private const val RUTA_DETALLE = "detalle/{id}"
private const val RUTA_CARRITO = "carrito"
private const val RUTA_CONFIRMAR = "confirmar"
private const val RUTA_EXITO = "exito/{id}"
private const val RUTA_PRODUCTO = "producto/{id}"

@Composable
fun PrincipalNavigation(authViewModel: AuthViewModel, alCerrarSesion: () -> Unit) {
    val navController = rememberNavController()
    val carritoViewModel: CarritoViewModel = viewModel()
    val pedidoViewModel: PedidoViewModel = viewModel()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val usuario by authViewModel.usuario.collectAsState()
    val cantidadCarrito by carritoViewModel.cantidadTotal.collectAsState()
    val esAdmin = usuario?.rol == "ADMIN"
    val barra = if (esAdmin) rutasAdmin else rutasBarra

    LaunchedEffect(usuario) {
        carritoViewModel.fijarUsuario(usuario?.id ?: -1)
        pedidoViewModel.fijarUsuario(usuario)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                barra.forEach { item ->
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
                if (esAdmin) {
                    GestionProductosScreen(
                        alNuevo = { navController.navigate("producto/-1") },
                        alEditar = { id -> navController.navigate("producto/$id") }
                    )
                } else {
                    CatalogoScreen(
                        cantidadCarrito = cantidadCarrito,
                        alAbrirProducto = { id -> navController.navigate("detalle/$id") },
                        alAbrirCarrito = { navController.navigate(RUTA_CARRITO) }
                    )
                }
            }
            composable(
                route = RUTA_PRODUCTO,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                ProductoFormScreen(alVolver = { navController.popBackStack() })
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
                    alVolver = { navController.popBackStack() },
                    alContinuar = { navController.navigate(RUTA_CONFIRMAR) }
                )
            }
            composable(RUTA_CONFIRMAR) {
                ConfirmarPedidoScreen(
                    carritoViewModel = carritoViewModel,
                    pedidoViewModel = pedidoViewModel,
                    alVolver = { navController.popBackStack() },
                    alConfirmar = { id ->
                        navController.navigate("exito/$id") {
                            popUpTo(RUTA_CARRITO) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = RUTA_EXITO,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { respaldo ->
                ExitoPedidoScreen(
                    idPedido = respaldo.arguments?.getInt("id") ?: 0,
                    alVerPedidos = {
                        navController.navigate(Ruta.Pedidos.ruta) {
                            popUpTo(Ruta.Inicio.ruta)
                        }
                    },
                    alSeguir = { navController.popBackStack(Ruta.Inicio.ruta, false) }
                )
            }
            composable(Ruta.Mapa.ruta) { MapaScreen() }
            composable(Ruta.Inventario.ruta) { InventarioScreen() }
            composable(Ruta.Escaner.ruta) {
                EscanerScreen(
                    alAbrirProducto = { id -> navController.navigate("detalle/$id") }
                )
            }
            composable(Ruta.Pedidos.ruta) { PedidosScreen(pedidoViewModel) }
            composable(Ruta.Perfil.ruta) { PerfilScreen(authViewModel, alCerrarSesion) }
        }
    }
}