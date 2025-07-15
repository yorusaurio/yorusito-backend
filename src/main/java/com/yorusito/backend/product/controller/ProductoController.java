package com.yorusito.backend.product.controller;

import com.yorusito.backend.product.dto.ProductoRequest;
import com.yorusito.backend.product.dto.ProductoResponse;
import com.yorusito.backend.product.service.ProductoService;
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

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

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

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Obtener productos por categoría", description = "Lista productos de una categoría específica")
    public ResponseEntity<List<ProductoResponse>> obtenerPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoriaId));
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id, 
                                                      @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar producto", description = "Desactiva un producto (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
