package com.yorusito.backend.product.controller;

import com.yorusito.backend.product.dto.ColeccionRequest;
import com.yorusito.backend.product.dto.ColeccionResponse;
import com.yorusito.backend.product.service.ColeccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Gestión de Colecciones", description = "CRUD de colecciones de productos")
public class ColeccionController {
    
    private final ColeccionService coleccionService;
    
    // Endpoints públicos para colecciones
    @GetMapping("/colecciones")
    @Operation(summary = "Listar colecciones activas", description = "Obtiene todas las colecciones activas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Colecciones obtenidas exitosamente")
    })
    public ResponseEntity<List<ColeccionResponse>> listarColeccionesPublicas() {
        List<ColeccionResponse> colecciones = coleccionService.listarColeccionesActivas();
        return ResponseEntity.ok(colecciones);
    }
    
    @GetMapping("/colecciones/destacadas")
    @Operation(summary = "Listar colecciones destacadas", description = "Obtiene las colecciones marcadas como destacadas")
    public ResponseEntity<List<ColeccionResponse>> listarColeccionesDestacadas() {
        List<ColeccionResponse> colecciones = coleccionService.listarColeccionesDestacadas();
        return ResponseEntity.ok(colecciones);
    }
    
    @GetMapping("/colecciones/{id}")
    @Operation(summary = "Obtener colección por ID", description = "Obtiene una colección específica por su ID")
    public ResponseEntity<ColeccionResponse> obtenerColeccion(
            @Parameter(description = "ID de la colección") @PathVariable Long id) {
        ColeccionResponse coleccion = coleccionService.obtenerPorId(id);
        return ResponseEntity.ok(coleccion);
    }
    
    // Endpoints de administración
    @GetMapping("/admin/colecciones")
    @Operation(summary = "Listar todas las colecciones (Admin)", description = "Obtiene todas las colecciones para administración")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ColeccionResponse>> listarTodasLasColecciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ColeccionResponse> colecciones = coleccionService.listarTodasPaginadas(PageRequest.of(page, size));
        return ResponseEntity.ok(colecciones);
    }
    
    @PostMapping("/admin/colecciones")
    @Operation(summary = "Crear colección", description = "Crea una nueva colección")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColeccionResponse> crearColeccion(@Valid @RequestBody ColeccionRequest request) {
        ColeccionResponse coleccion = coleccionService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(coleccion);
    }
    
    @PutMapping("/admin/colecciones/{id}")
    @Operation(summary = "Actualizar colección", description = "Actualiza una colección existente")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColeccionResponse> actualizarColeccion(
            @Parameter(description = "ID de la colección") @PathVariable Long id,
            @Valid @RequestBody ColeccionRequest request) {
        ColeccionResponse coleccion = coleccionService.actualizar(id, request);
        return ResponseEntity.ok(coleccion);
    }
    
    @DeleteMapping("/admin/colecciones/{id}")
    @Operation(summary = "Eliminar colección", description = "Elimina una colección")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarColeccion(
            @Parameter(description = "ID de la colección") @PathVariable Long id) {
        coleccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/admin/colecciones/{id}/toggle-activa")
    @Operation(summary = "Activar/Desactivar colección", description = "Cambia el estado activo de una colección")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColeccionResponse> toggleActivaColeccion(
            @Parameter(description = "ID de la colección") @PathVariable Long id) {
        ColeccionResponse coleccion = coleccionService.toggleActiva(id);
        return ResponseEntity.ok(coleccion);
    }
    
    @PutMapping("/admin/colecciones/{id}/toggle-destacada")
    @Operation(summary = "Destacar/No destacar colección", description = "Cambia el estado destacado de una colección")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColeccionResponse> toggleDestacadaColeccion(
            @Parameter(description = "ID de la colección") @PathVariable Long id) {
        ColeccionResponse coleccion = coleccionService.toggleDestacada(id);
        return ResponseEntity.ok(coleccion);
    }
}
