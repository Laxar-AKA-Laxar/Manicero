package com.example.manicero.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.manicero.view.BienvenidaScreen
import com.example.manicero.view.LoginScreen
import com.example.manicero.view.RegistroScreen
import com.example.manicero.viewmodel.AuthViewModel

private fun NavHostController.irAPrincipal() {
    navigate(Destino.PRINCIPAL) { popUpTo(0) { inclusive = true } }
}

private fun NavHostController.irABienvenida() {
    navigate(Destino.BIENVENIDA) { popUpTo(0) { inclusive = true } }
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val listo by authViewModel.listo.collectAsState()

    if (!listo) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val inicio = remember {
        if (authViewModel.usuario.value != null) Destino.PRINCIPAL else Destino.BIENVENIDA
    }

    NavHost(
        navController = navController,
        startDestination = inicio,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition = { fadeOut(tween(200)) }
    ) {
        composable(Destino.BIENVENIDA) {
            BienvenidaScreen(
                alComenzar = { navController.navigate(Destino.LOGIN) },
                alCrearCuenta = { navController.navigate(Destino.REGISTRO) }
            )
        }
        composable(Destino.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                alEntrar = { navController.irAPrincipal() },
                alRegistrar = { navController.navigate(Destino.REGISTRO) }
            )
        }
        composable(Destino.REGISTRO) {
            RegistroScreen(
                viewModel = authViewModel,
                alRegistrar = { navController.irAPrincipal() },
                alIrALogin = { navController.navigate(Destino.LOGIN) }
            )
        }
        composable(Destino.PRINCIPAL) {
            PrincipalNavigation(
                authViewModel = authViewModel,
                alCerrarSesion = {
                    authViewModel.cerrarSesion()
                    navController.irABienvenida()
                }
            )
        }
    }
}