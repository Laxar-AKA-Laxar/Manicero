package com.example.manicero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.manicero.navigation.AppNavigation
import com.example.manicero.ui.theme.ManiceroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManiceroTheme {
                AppNavigation()
            }
        }
    }
}