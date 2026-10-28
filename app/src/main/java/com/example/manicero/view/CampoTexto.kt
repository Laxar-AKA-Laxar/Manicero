package com.example.manicero.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CampoTexto(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    iconoInicial: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    esClave: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
    accion: ImeAction = ImeAction.Next
) {
    var oculta by remember { mutableStateOf(true) }
    val valido = error == null && valor.isNotEmpty()

    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier.fillMaxWidth().animateContentSize(),
        label = { Text(etiqueta) },
        leadingIcon = { Icon(iconoInicial, contentDescription = null) },
        trailingIcon = {
            when {
                esClave -> TextButton(onClick = { oculta = !oculta }) {
                    Text(if (oculta) "Ver" else "Ocultar")
                }
                error != null -> Icon(
                    Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
                valido -> Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Correcto",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        supportingText = error?.let { mensaje -> { Text(mensaje) } },
        isError = error != null,
        singleLine = true,
        visualTransformation = if (esClave && oculta) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esClave) KeyboardType.Password else teclado,
            imeAction = accion
        )
    )
}