package com.example.manicero.view

import java.util.Locale

fun precioChileno(valor: Int): String =
    "$" + "%,d".format(Locale.forLanguageTag("es-CL"), valor)