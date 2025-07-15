# Yorusito Backend - Sistema E-commerce

API REST completa para sistema de e-commerce de ropa desarrollada en Spring Boot 3.

## 🚀 Características

- ✅ **Autenticación JWT** - Registro y login seguro
- ✅ **Gestión de Productos** - CRUD completo con categorías
- ✅ **Carrito de Compras** - Agregar, eliminar y gestionar productos
- ✅ **Sistema de Pedidos** - Confirmación y seguimiento de pedidos
- ✅ **Control de Stock** - Actualización automática de inventario
- ✅ **Roles de Usuario** - USER y ADMIN con permisos diferenciados
- ✅ **Documentación Swagger** - API completamente documentada
- ✅ **Manejo de Errores** - Respuestas estructuradas de errores
- ✅ **Base de Datos H2** - Para desarrollo y pruebas

## 🛠 Tecnologías

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **JWT** - Tokens de autenticación
- **H2 Database** - Base de datos en memoria
- **OpenAPI/Swagger** - Documentación de API
- **Lombok** - Reducción de código boilerplate
- **MapStruct** - Mapeo de objetos

## 📋 Requisitos

- Java 17 o superior
- Maven 3.6 o superior

## 🔧 Instalación y Configuración

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/yorusito-backend.git
cd yorusito-backend
```

2. **Compilar el proyecto**
```bash
mvn clean compile
```

3. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

4. **Acceder a la aplicación**
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

## 📊 Base de Datos

### Configuración H2
- **URL**: `jdbc:h2:mem:yorusito`
- **Usuario**: `sa`
- **Contraseña**: *(vacía)*

### Datos de Prueba
El sistema incluye datos de prueba:

**Usuarios:**
- Admin: `admin@yorusito.com` / `password`
- Usuario: `usuario@demo.com` / `password`

**Productos:** 13 productos en 5 categorías (Polos, Poleras, Jeans, Camisas, Vestidos)

## 🔐 Autenticación

### Registrar Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "nombre": "Usuario Prueba",
  "email": "usuario@test.com",
  "password": "password123",
  "telefono": "999888777"
}
```

### Iniciar Sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@test.com",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "id": 1,
  "nombre": "Usuario Prueba",
  "email": "usuario@test.com",
  "rol": "USER"
}
```

## 📡 Endpoints Principales

### Productos (Público)
- `GET /api/productos` - Lista productos (paginado)
- `GET /api/productos/lista` - Lista todos los productos
- `GET /api/productos/{id}` - Obtener producto por ID
- `GET /api/productos/buscar?search=polo` - Buscar productos
- `GET /api/productos/categoria/{id}` - Productos por categoría

### Categorías (Público)
- `GET /api/categorias` - Lista categorías
- `GET /api/categorias/{id}` - Obtener categoría por ID

### Carrito (Autenticado)
- `GET /api/carrito` - Obtener carrito del usuario
- `POST /api/carrito/agregar` - Agregar producto al carrito
- `DELETE /api/carrito/eliminar/{id}` - Eliminar producto del carrito
- `DELETE /api/carrito/vaciar` - Vaciar carrito completo

### Pedidos (Autenticado)
- `POST /api/pedidos` - Crear pedido desde carrito
- `GET /api/pedidos` - Lista pedidos del usuario
- `GET /api/pedidos/{id}` - Obtener pedido por ID

### Administración (Solo ADMIN)
- `POST /api/productos` - Crear producto
- `PUT /api/productos/{id}` - Actualizar producto
- `DELETE /api/productos/{id}` - Eliminar producto
- `POST /api/categorias` - Crear categoría
- `PUT /api/categorias/{id}` - Actualizar categoría
- `DELETE /api/categorias/{id}` - Eliminar categoría
- `GET /api/pedidos/admin/todos` - Ver todos los pedidos
- `PUT /api/pedidos/admin/{id}/estado` - Actualizar estado de pedido

## 🛒 Flujo de Compra

1. **Explorar productos** (sin autenticación)
2. **Registrarse/Iniciar sesión**
3. **Agregar productos al carrito**
4. **Confirmar pedido** con dirección de envío
5. **Seguimiento del pedido** por estados

## 📝 Ejemplos de Uso

### Agregar Producto al Carrito
```http
POST /api/carrito/agregar
Authorization: Bearer {token}
Content-Type: application/json

{
  "productoId": 1,
  "cantidad": 2
}
```

### Crear Pedido
```http
POST /api/pedidos
Authorization: Bearer {token}
Content-Type: application/json

{
  "direccionEnvio": "Av. Principal 123, Ciudad",
  "telefonoContacto": "999888777",
  "observaciones": "Entregar en horario de oficina"
}
```

### Crear Producto (Admin)
```http
POST /api/productos
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "nombre": "Polo Nuevo",
  "descripcion": "Descripción del producto",
  "precio": 45.90,
  "stock": 100,
  "imagenUrl": "https://example.com/imagen.jpg",
  "categoriaId": 1
}
```

## 🏗 Arquitectura

```
src/main/java/com/yorusito/backend/
├── auth/                   # Módulo de autenticación
│   ├── controller/         # Controladores REST
│   ├── dto/               # DTOs de request/response
│   ├── entity/            # Entidades JPA
│   ├── repository/        # Repositorios de datos
│   ├── security/          # Configuración JWT
│   └── service/           # Lógica de negocio
├── order/                 # Módulo de pedidos y carrito
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── product/               # Módulo de productos
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
└── shared/                # Componentes compartidos
    ├── config/            # Configuraciones
    ├── enums/             # Enumerados
    ├── exception/         # Manejo de errores
    └── util/              # Utilidades
```

## 🔄 Estados de Pedido

- `PENDIENTE` - Pedido creado, esperando confirmación
- `CONFIRMADO` - Pedido confirmado por el sistema
- `EN_PREPARACION` - Pedido siendo preparado
- `ENVIADO` - Pedido enviado al cliente
- `ENTREGADO` - Pedido entregado exitosamente
- `CANCELADO` - Pedido cancelado

## 🚀 Próximas Mejoras

- [ ] **Sistema de Reseñas** - Calificaciones y comentarios de productos
- [ ] **Direcciones de Envío** - Múltiples direcciones por usuario
- [ ] **Historial de Estados** - Seguimiento detallado de pedidos
- [ ] **Notificaciones** - Emails y SMS de confirmación
- [ ] **Descuentos y Cupones** - Sistema de promociones
- [ ] **Wishlist** - Lista de productos favoritos
- [ ] **Filtros Avanzados** - Búsqueda por precio, talla, color
- [ ] **Upload de Imágenes** - Gestión de archivos multimedia
- [ ] **Reportes** - Dashboard administrativo
- [ ] **API Rate Limiting** - Control de límites de uso

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 🤝 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📞 Contacto

- **Email**: contacto@yorusito.com
- **Proyecto**: [https://github.com/tu-usuario/yorusito-backend](https://github.com/tu-usuario/yorusito-backend)
