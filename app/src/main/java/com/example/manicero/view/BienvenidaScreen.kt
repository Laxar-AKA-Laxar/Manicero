package com.example.manicero.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BienvenidaScreen(alComenzar: () -> Unit, alCrearCuenta: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(900)) + scaleIn(tween(900), initialScale = 0.8f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "OYL",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "FRUTOS SECOS",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Productos naturales más cerca de ti",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(900, delayMillis = 400)) +
                    slideInVertically(tween(900, delayMillis = 400)) { it / 2 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = alComenzar, modifier = Modifier.fillMaxWidth()) {
                    Text("Comenzar")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = alCrearCuenta, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear una cuenta")
                }
            }
        }
    }
}