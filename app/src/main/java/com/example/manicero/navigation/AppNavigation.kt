package com.example.manicero.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.manicero.view.CatalogoScreen
import com.example.manicero.view.PantallaPendiente

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

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
            composable(Ruta.Inicio.ruta) { CatalogoScreen() }
            composable(Ruta.Mapa.ruta) { PantallaPendiente("Mapa de tiendas") }
            composable(Ruta.Escaner.ruta) { PantallaPendiente("Escáner QR") }
            composable(Ruta.Pedidos.ruta) { PantallaPendiente("Mis pedidos") }
            composable(Ruta.Perfil.ruta) { PantallaPendiente("Mi perfil") }
        }
    }
}