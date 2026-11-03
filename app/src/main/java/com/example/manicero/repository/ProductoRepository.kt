package com.example.manicero.repository

import com.example.manicero.data.ProductoDao
import com.example.manicero.model.Producto
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ProductoRepository(private val dao: ProductoDao) {

    val productos: Flow<List<Producto>> = dao.obtenerTodos()

    fun observarPorId(id: Int): Flow<Producto?> = dao.observarPorId(id)

    suspend fun buscarPorId(id: Int): Producto? = dao.buscarPorId(id)

    suspend fun buscarPorQr(codigo: String): Producto? = dao.buscarPorQr(codigo)

    suspend fun guardar(producto: Producto) = dao.insertar(producto)

    suspend fun actualizar(producto: Producto) = dao.actualizar(producto)

    suspend fun cambiarActivo(id: Int, activo: Boolean) = dao.cambiarActivo(id, activo)

    suspend fun ajustarStock(id: Int, cambio: Int) = dao.ajustarStock(id, cambio)

    suspend fun nuevoCodigo(): String =
        "OYL-" + String.format(Locale.US, "%03d", dao.maximoId() + 1)

    suspend fun cargarDatosDePrueba() {
        if (dao.obtenerTodos().first().isEmpty()) {
            dao.insertarTodos(datosDePrueba)
        }
    }

    private val datosDePrueba = listOf(
        Producto(
            nombre = "Almendras",
            categoria = "Frutos secos",
            precio = 2990,
            stock = 25,
            codigoQr = "OYL-001",
            presentacion = "250 g",
            descripcion = "Almendras naturales de alta calidad"
        ),
        Producto(
            nombre = "Nueces",
            categoria = "Frutos secos",
            precio = 3490,
            stock = 18,
            codigoQr = "OYL-002",
            presentacion = "250 g",
            descripcion = "Nueces peladas, ricas en grasas buenas"
        ),
        Producto(
            nombre = "Mix Premium",
            categoria = "Frutos secos",
            precio = 3990,
            stock = 40,
            codigoQr = "OYL-003",
            presentacion = "250 g",
            descripcion = "Mezcla de frutos secos seleccionados",
            stockMinimo = 10
        ),
        Producto(
            nombre = "Semillas de Chía",
            categoria = "Semillas",
            precio = 2290,
            stock = 22,
            codigoQr = "OYL-004",
            presentacion = "200 g",
            descripcion = "Semillas de chía ricas en fibra"
        ),
        Producto(
            nombre = "Pasas",
            categoria = "Deshidratados",
            precio = 2490,
            stock = 30,
            codigoQr = "OYL-005",
            presentacion = "250 g",
            descripcion = "Pasas rubias sin semilla"
        ),
        Producto(
            nombre = "Cranberries",
            categoria = "Deshidratados",
            precio = 2890,
            stock = 16,
            codigoQr = "OYL-006",
            presentacion = "200 g",
            descripcion = "Cranberries deshidratados"
        )
    )
}