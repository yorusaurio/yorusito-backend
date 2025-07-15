package com.yorusito.backend.product.controller;

import com.yorusito.backend.product.dto.CategoriaRequest;
import com.yorusito.backend.product.dto.CategoriaResponse;
import com.yorusito.backend.product.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Obtener todas las categorías", description = "Lista todas las categorías disponibles")
    public ResponseEntity<List<CategoriaResponse>> obtenerTodas() {
        return ResponseEntity.ok(categoriaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Obtiene una categoría específica por su ID")
    public ResponseEntity<CategoriaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoriaResponse> actualizar(@PathVariable Long id, 
                                                       @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría (solo administradores)",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
