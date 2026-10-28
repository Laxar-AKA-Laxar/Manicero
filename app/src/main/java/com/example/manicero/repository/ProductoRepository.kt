package com.example.manicero.repository

import com.example.manicero.data.ProductoDao
import com.example.manicero.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ProductoRepository(private val dao: ProductoDao) {

    val productos: Flow<List<Producto>> = dao.obtenerTodos()

    suspend fun buscarPorId(id: Int): Producto? = dao.buscarPorId(id)

    suspend fun buscarPorQr(codigo: String): Producto? = dao.buscarPorQr(codigo)

    suspend fun guardar(producto: Producto) = dao.insertar(producto)

    suspend fun actualizar(producto: Producto) = dao.actualizar(producto)

    suspend fun cargarDatosDePrueba() {
        if (dao.obtenerTodos().first().isEmpty()) {
            dao.insertarTodos(datosDePrueba)
        }
    }

    private val datosDePrueba = listOf(
        Producto(nombre = "Almendras", categoria = "Frutos secos", precio = 2990, stock = 25, codigoQr = "OYL-001"),
        Producto(nombre = "Nueces", categoria = "Frutos secos", precio = 3490, stock = 18, codigoQr = "OYL-002"),
        Producto(nombre = "Mix Premium", categoria = "Frutos secos", precio = 3990, stock = 40, codigoQr = "OYL-003"),
        Producto(nombre = "Semillas de Chía", categoria = "Semillas", precio = 2290, stock = 22, codigoQr = "OYL-004"),
        Producto(nombre = "Pasas", categoria = "Deshidratados", precio = 2490, stock = 30, codigoQr = "OYL-005"),
        Producto(nombre = "Cranberries", categoria = "Deshidratados", precio = 2890, stock = 16, codigoQr = "OYL-006")
    )
}