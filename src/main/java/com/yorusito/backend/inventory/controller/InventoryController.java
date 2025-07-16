package com.yorusito.backend.inventory.controller;

import com.yorusito.backend.inventory.dto.*;
import com.yorusito.backend.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de inventario y stock")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @PostMapping("/movements")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear movimiento de inventario", description = "Registra un movimiento de inventario (entrada, salida, ajuste)")
    public ResponseEntity<InventoryMovementResponse> createMovement(
            Authentication authentication,
            @Valid @RequestBody InventoryMovementRequest request) {
        InventoryMovementResponse movement = inventoryService.createMovement(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(movement);
    }
    
    @GetMapping("/movements")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener movimientos de inventario", description = "Obtiene el historial de movimientos de inventario")
    public ResponseEntity<Page<InventoryMovementResponse>> getMovements(
            @RequestParam(required = false) Long productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryMovementResponse> movements = inventoryService.getMovements(productoId, pageable);
        return ResponseEntity.ok(movements);
    }
    
    @GetMapping("/movements/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener movimientos por rango de fechas", description = "Obtiene movimientos de inventario en un rango de fechas")
    public ResponseEntity<List<InventoryMovementResponse>> getMovementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<InventoryMovementResponse> movements = inventoryService.getMovementsByDateRange(startDate, endDate);
        return ResponseEntity.ok(movements);
    }
    
    @GetMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener stock de todos los productos", description = "Obtiene el stock actual de todos los productos")
    public ResponseEntity<List<ProductStockResponse>> getAllProductsStock() {
        List<ProductStockResponse> stock = inventoryService.getAllProductsStock();
        return ResponseEntity.ok(stock);
    }
    
    @GetMapping("/stock/low")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener productos con stock bajo", description = "Obtiene productos con stock bajo o agotado")
    public ResponseEntity<List<ProductStockResponse>> getLowStockProducts() {
        List<ProductStockResponse> lowStock = inventoryService.getLowStockProducts();
        return ResponseEntity.ok(lowStock);
    }
    
    @GetMapping("/alerts")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener alertas activas", description = "Obtiene todas las alertas de inventario activas")
    public ResponseEntity<List<InventoryAlertResponse>> getActiveAlerts() {
        List<InventoryAlertResponse> alerts = inventoryService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @PutMapping("/alerts/{alertId}/notify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Marcar alerta como notificada", description = "Marca una alerta como notificada")
    public ResponseEntity<Void> markAlertAsNotified(@PathVariable Long alertId) {
        inventoryService.markAlertAsNotified(alertId);
        return ResponseEntity.ok().build();
    }
}
