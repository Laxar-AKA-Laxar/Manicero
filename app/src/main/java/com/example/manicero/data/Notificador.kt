package com.example.manicero.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.manicero.MainActivity

class Notificador(private val contexto: Context) {

    init {
        val canal = NotificationChannel(CANAL, "Pedidos y avisos", NotificationManager.IMPORTANCE_DEFAULT)
        contexto.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    fun mostrar(id: Int, titulo: String, mensaje: String) {
        val permitido = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        if (!permitido) return

        val intento = Intent(contexto, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendiente = PendingIntent.getActivity(
            contexto,
            id,
            intento,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val aviso = NotificationCompat.Builder(contexto, CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setAutoCancel(true)
            .setContentIntent(pendiente)
            .build()
        try {
            NotificationManagerCompat.from(contexto).notify(id, aviso)
        } catch (e: SecurityException) {
            return
        }
    }

    companion object {
        private const val CANAL = "pedidos"
    }
}