package com.yorusito.backend.product.controller;

import com.yorusito.backend.product.dto.ProductoRequest;
import com.yorusito.backend.product.dto.ProductoResponse;
import com.yorusito.backend.product.service.ProductoService;
import com.yorusito.backend.image.service.ImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de productos")
public class ProductoController {

    private final ProductoService productoService;
    private final ImageUploadService imageUploadService;

    @GetMapping
    @Operation(summary = "Obtener productos", description = "Lista productos con paginación opcional")
    public ResponseEntity<Page<ProductoResponse>> obtenerTodos(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productoService.obtenerTodosPaginado(pageable));
    }

    @GetMapping("/lista")
    @Operation(summary = "Obtener todos los productos", description = "Lista todos los productos activos sin paginación")
    public ResponseEntity<List<ProductoResponse>> obtenerTodosLista() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos", description = "Busca productos por nombre o descripción")
    public ResponseEntity<Page<ProductoResponse>> buscarProductos(
            @Parameter(description = "Término de búsqueda") @RequestParam String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productoService.buscarProductos(search, pageable));
    }

    @GetMapping("/buscar/avanzada")
    @Operation(summary = "Búsqueda avanzada de productos", description = "Búsqueda avanzada con filtros múltiples")
    public ResponseEntity<Page<ProductoResponse>> busquedaAvanzada(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) java.math.BigDecimal precioMin,
            @RequestParam(required = false) java.math.BigDecimal precioMax,
            @RequestParam(required = false) Boolean enStock,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productoService.busquedaAvanzada(search, categoriaId, precioMin, precioMax, enStock, ordenarPor, direccion, pageable));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Obtener productos por categoría", description = "Lista productos de una categoría específica")
    public ResponseEntity<List<ProductoResponse>> obtenerPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoriaId));
    }

    @GetMapping("/new-arrivals")
    @Operation(summary = "Obtener nuevos llegados", description = "Lista los productos más recientemente agregados")
    public ResponseEntity<List<ProductoResponse>> obtenerNuevosLlegados(
            @Parameter(description = "Número máximo de productos a retornar") 
            @RequestParam(defaultValue = "10") int limite) {
        if (limite > 50) {
            limite = 50; // Limitar máximo a 50 productos para evitar sobrecarga
        }
        return ResponseEntity.ok(productoService.obtenerNuevosLlegados(limite));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico por su ID")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }
    
    @PostMapping("/con-imagen")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear producto con imagen", description = "Crea un nuevo producto subiendo imagen automáticamente (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductoResponse> crearConImagen(
            @Parameter(description = "Imagen del producto") @RequestParam("imagen") MultipartFile imagen,
            @Parameter(description = "Nombre del producto") @RequestParam("nombre") String nombre,
            @Parameter(description = "Descripción del producto") @RequestParam(value = "descripcion", required = false) String descripcion,
            @Parameter(description = "Precio del producto") @RequestParam("precio") BigDecimal precio,
            @Parameter(description = "Stock del producto") @RequestParam("stock") Integer stock,
            @Parameter(description = "ID de la categoría") @RequestParam("categoriaId") Long categoriaId,
            @Parameter(description = "ID de la colección") @RequestParam(value = "coleccionId", required = false) Long coleccionId) {
        
        try {
            // 1. Subir imagen primero
            String imagenUrl = imageUploadService.uploadImage(imagen);
            
            // 2. Crear el request con la URL de la imagen
            ProductoRequest request = ProductoRequest.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .precio(precio)
                    .stock(stock)
                    .imagenUrl(imagenUrl)
                    .categoriaId(categoriaId)
                    .coleccionId(coleccionId)
                    .build();
            
            // 3. Crear el producto
            ProductoResponse producto = productoService.crear(request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(producto);
            
        } catch (Exception e) {
            throw new RuntimeException("Error procesando imagen: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id, 
                                                      @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }
    
    @PutMapping("/{id}/con-imagen")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar producto con imagen", description = "Actualiza un producto existente con nueva imagen (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductoResponse> actualizarConImagen(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @Parameter(description = "Nueva imagen del producto (opcional)") @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @Parameter(description = "Nombre del producto") @RequestParam("nombre") String nombre,
            @Parameter(description = "Descripción del producto") @RequestParam(value = "descripcion", required = false) String descripcion,
            @Parameter(description = "Precio del producto") @RequestParam("precio") BigDecimal precio,
            @Parameter(description = "Stock del producto") @RequestParam("stock") Integer stock,
            @Parameter(description = "ID de la categoría") @RequestParam("categoriaId") Long categoriaId,
            @Parameter(description = "ID de la colección") @RequestParam(value = "coleccionId", required = false) Long coleccionId,
            @Parameter(description = "URL de imagen actual (si no se sube nueva)") @RequestParam(value = "imagenUrl", required = false) String imagenUrlActual) {
        
        try {
            String imagenUrl = imagenUrlActual; // Usar la URL actual por defecto
            
            // Si se proporciona una nueva imagen, subirla
            if (imagen != null && !imagen.isEmpty()) {
                imagenUrl = imageUploadService.uploadImage(imagen);
            }
            
            // Crear el request con los datos actualizados
            ProductoRequest request = ProductoRequest.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .precio(precio)
                    .stock(stock)
                    .imagenUrl(imagenUrl)
                    .categoriaId(categoriaId)
                    .coleccionId(coleccionId)
                    .build();
            
            // Actualizar el producto
            ProductoResponse producto = productoService.actualizar(id, request);
            
            return ResponseEntity.ok(producto);
            
        } catch (Exception e) {
            throw new RuntimeException("Error procesando imagen: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar producto", description = "Desactiva un producto (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/populares")
    @Operation(summary = "Productos populares", description = "Obtiene productos populares basado en reviews")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosPopulares(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productoService.obtenerProductosPopulares(limit));
    }

    @GetMapping("/relacionados/{id}")
    @Operation(summary = "Productos relacionados", description = "Obtiene productos relacionados basado en la categoría")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosRelacionados(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerProductosRelacionados(id));
    }
}
