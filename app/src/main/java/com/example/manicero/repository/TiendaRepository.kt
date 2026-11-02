package com.example.manicero.repository

import com.example.manicero.model.Tienda

object TiendaRepository {

    val tiendas = listOf(
        Tienda(
            id = 1,
            nombre = "Tienda OYL - Santiago Centro",
            tipo = "Tienda",
            direccion = "Av. Libertador B. O'Higgins 1234, Santiago",
            horario = "Lun a Sáb, 8:00 - 19:00",
            latitud = -33.4450,
            longitud = -70.6600
        ),
        Tienda(
            id = 2,
            nombre = "Punto de retiro - Providencia",
            tipo = "Punto de retiro",
            direccion = "Av. Providencia 1500, Providencia",
            horario = "Lun a Vie, 10:00 - 18:00",
            latitud = -33.4263,
            longitud = -70.6110
        ),
        Tienda(
            id = 3,
            nombre = "Punto de retiro - Plaza Oeste",
            tipo = "Punto de retiro",
            direccion = "Av. Américo Vespucio 1501, Cerrillos",
            horario = "Lun a Dom, 10:00 - 20:00",
            latitud = -33.4967,
            longitud = -70.7183
        )
    )
}