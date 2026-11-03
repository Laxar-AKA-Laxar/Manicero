# Manicero

Aplicación móvil Android para Manaicero, emprendimiento de venta de frutos secos, semillas y deshidratados. Proyecto de la asignatura DSY1105 Desarrollo de Aplicaciones Móviles, Evaluación Parcial 2.

## Integrantes

- Luciano Diaz
- Luciano Roca

## Problema que resuelve

La tienda gestiona su stock de forma manual entre el local físico y sus canales en línea. Esto produce descuadres y ventas de productos agotados. La app centraliza el catálogo, los pedidos y el control de inventario.

## Funcionalidades implementadas

- Pantalla de bienvenida con animación de entrada.
- Registro de clientes con validación por campo, íconos y mensajes de error.
- Inicio de sesión por rol, con sesión local persistente.
- Catálogo por categorías con búsqueda y filtros.
- Detalle de producto con selector de cantidad y control de stock.
- Carrito por cliente, guardado en Room.
- Confirmación de pedido con descuento transaccional de stock.
- Historial de pedidos con filtro por estado.
- Escáner QR de productos con CameraX y ML Kit.
- Mapa de tiendas y puntos de retiro, con ubicación del usuario.
- Gestión de productos para el administrador: crear, editar, activar y desactivar.
- Gestión de inventario con entradas y salidas, y alerta de stock bajo.
- Cambio de estado de pedidos por el administrador.
- Notificaciones locales con permiso de Android.

## Tecnologías

- Kotlin y Jetpack Compose con Material Design 3.
- Arquitectura MVVM con StateFlow.
- Navigation Compose.
- Room para persistencia local.
- CameraX y ML Kit para el escáner.
- osmdroid para el mapa, sin llaves de API.

## Estructura del proyecto

- `model`: entidades y estados.
- `data`: base de datos Room, DAOs y utilidades.
- `repository`: acceso a los datos.
- `viewmodel`: lógica de estado y validaciones.
- `view`: pantallas en Compose.
- `navigation`: rutas y barra inferior.

## Recursos nativos

- Cámara, para leer códigos QR.
- Ubicación, para mostrar la posición del usuario en el mapa.
- Notificaciones del sistema.

## Cuentas de prueba

- Administrador: alias `admin`, contraseña `admin123`.
- Clientes: se crean desde la pantalla de registro.
- Códigos QR de prueba: `OYL-001` a `OYL-006`.

Todos los datos son ficticios.

## Pasos para ejecutar

1. Clonar el repositorio.
2. Abrir la carpeta en Android Studio.
3. Esperar a que termine el Gradle sync.
4. Crear o elegir un emulador, o conectar un teléfono.
5. Pulsar Run.
6. Conceder los permisos de cámara, ubicación y notificaciones cuando se soliciten.

## Notas

- Es un MVP académico. No incluye publicación en tiendas.
- Las notificaciones son locales. Las notificaciones push y el servidor Spring Boot quedan para la evaluación final.