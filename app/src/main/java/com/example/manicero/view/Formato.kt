package com.example.manicero.view

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun precioChileno(valor: Int): String =
    "$" + "%,d".format(Locale.forLanguageTag("es-CL"), valor)

fun numeroPedido(id: Int): String = "#" + String.format(Locale.US, "%06d", id)

fun fechaPedido(milisegundos: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(Date(milisegundos))