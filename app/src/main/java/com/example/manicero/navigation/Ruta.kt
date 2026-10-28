package com.example.manicero.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Ruta(val ruta: String, val titulo: String, val icono: ImageVector) {
    data object Inicio : Ruta("inicio", "Inicio", Icons.Default.Home)
    data object Mapa : Ruta("mapa", "Mapa", Icons.Default.LocationOn)
    data object Escaner : Ruta("escaner", "Escáner", Icons.Default.Search)
    data object Pedidos : Ruta("pedidos", "Pedidos", Icons.AutoMirrored.Filled.List)
    data object Perfil : Ruta("perfil", "Perfil", Icons.Default.Person)
}

val rutasBarra = listOf(Ruta.Inicio, Ruta.Mapa, Ruta.Escaner, Ruta.Pedidos, Ruta.Perfil)