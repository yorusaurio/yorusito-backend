package com.yorusito.backend.inventory.service;

import com.yorusito.backend.inventory.dto.*;
import com.yorusito.backend.inventory.entity.InventoryAlert;
import com.yorusito.backend.inventory.entity.InventoryMovement;
import com.yorusito.backend.inventory.repository.InventoryAlertRepository;
import com.yorusito.backend.inventory.repository.InventoryMovementRepository;
import com.yorusito.backend.product.entity.Producto;
import com.yorusito.backend.product.repository.ProductoRepository;
import com.yorusito.backend.shared.exception.BadRequestException;
import com.yorusito.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryMovementRepository movementRepository;
    private final InventoryAlertRepository alertRepository;
    private final ProductoRepository productoRepository;
    
    @Transactional
    public InventoryMovementResponse createMovement(InventoryMovementRequest request, String userEmail) {
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        Integer stockAnterior = producto.getStock();
        Integer stockActual;
        
        switch (request.getTipo()) {
            case ENTRADA:
                stockActual = stockAnterior + request.getCantidad();
                break;
            case SALIDA:
                if (stockAnterior < request.getCantidad()) {
                    throw new BadRequestException("No hay suficiente stock disponible");
                }
                stockActual = stockAnterior - request.getCantidad();
                break;
            case AJUSTE:
                stockActual = request.getCantidad();
                break;
            default:
                stockActual = stockAnterior;
        }
        
        // Actualizar stock del producto
        producto.setStock(stockActual);
        productoRepository.save(producto);
        
        // Crear movimiento
        InventoryMovement movement = InventoryMovement.builder()
                .producto(producto)
                .tipo(request.getTipo())
                .cantidad(request.getCantidad())
                .stockAnterior(stockAnterior)
                .stockActual(stockActual)
                .motivo(request.getMotivo())
                .referenciaPedido(request.getReferenciaPedido())
                .usuarioResponsable(userEmail)
                .build();
        
        movement = movementRepository.save(movement);
        
        // Verificar alertas de stock bajo
        checkStockAlerts(producto);
        
        return mapToMovementResponse(movement);
    }
    
    @Transactional(readOnly = true)
    public Page<InventoryMovementResponse> getMovements(Long productoId, Pageable pageable) {
        Page<InventoryMovement> movements;
        
        if (productoId != null) {
            movements = movementRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId, pageable);
        } else {
            movements = movementRepository.findAll(pageable);
        }
        
        return movements.map(this::mapToMovementResponse);
    }
    
    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> getMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<InventoryMovement> movements = movementRepository.findByFechaMovimientoBetween(startDate, endDate);
        return movements.stream()
                .map(this::mapToMovementResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductStockResponse> getAllProductsStock() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
                .map(this::mapToProductStockResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductStockResponse> getLowStockProducts() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
                .filter(p -> p.getStock() <= 10) // Consideramos stock bajo cuando es <= 10
                .map(this::mapToProductStockResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<InventoryAlertResponse> getActiveAlerts() {
        List<InventoryAlert> alerts = alertRepository.findByActivaTrueOrderByFechaAlertaDesc();
        return alerts.stream()
                .map(this::mapToAlertResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void markAlertAsNotified(Long alertId) {
        InventoryAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada"));
        
        alert.setNotificada(true);
        alertRepository.save(alert);
    }
    
    private void checkStockAlerts(Producto producto) {
        Integer stockMinimo = 10; // Valor por defecto, se puede configurar por producto
        
        if (producto.getStock() <= stockMinimo) {
            // Verificar si ya existe una alerta activa para este producto
            if (!alertRepository.findByProductoIdAndActivaTrue(producto.getId()).isPresent()) {
                InventoryAlert alert = InventoryAlert.builder()
                        .producto(producto)
                        .stockMinimo(stockMinimo)
                        .stockActual(producto.getStock())
                        .mensaje(producto.getStock() == 0 ? 
                                "Producto agotado" : 
                                "Stock bajo: " + producto.getStock() + " unidades restantes")
                        .build();
                
                alertRepository.save(alert);
            }
        } else {
            // Si el stock se recupera, desactivar alertas existentes
            alertRepository.findByProductoIdAndActivaTrue(producto.getId())
                    .ifPresent(alert -> {
                        alert.setActiva(false);
                        alertRepository.save(alert);
                    });
        }
    }
    
    private InventoryMovementResponse mapToMovementResponse(InventoryMovement movement) {
        return InventoryMovementResponse.builder()
                .id(movement.getId())
                .productoId(movement.getProducto().getId())
                .nombreProducto(movement.getProducto().getNombre())
                .tipo(movement.getTipo())
                .cantidad(movement.getCantidad())
                .stockAnterior(movement.getStockAnterior())
                .stockActual(movement.getStockActual())
                .motivo(movement.getMotivo())
                .referenciaPedido(movement.getReferenciaPedido())
                .fechaMovimiento(movement.getFechaMovimiento())
                .usuarioResponsable(movement.getUsuarioResponsable())
                .build();
    }
    
    private ProductStockResponse mapToProductStockResponse(Producto producto) {
        Integer stockMinimo = 10; // Valor por defecto
        
        return ProductStockResponse.builder()
                .productoId(producto.getId())
                .nombreProducto(producto.getNombre())
                .stockActual(producto.getStock())
                .stockMinimo(stockMinimo)
                .stockBajo(producto.getStock() <= stockMinimo)
                .stockAgotado(producto.getStock() == 0)
                .categoria(producto.getCategoria().getNombre())
                .build();
    }
    
    private InventoryAlertResponse mapToAlertResponse(InventoryAlert alert) {
        return InventoryAlertResponse.builder()
                .id(alert.getId())
                .productoId(alert.getProducto().getId())
                .nombreProducto(alert.getProducto().getNombre())
                .stockMinimo(alert.getStockMinimo())
                .stockActual(alert.getStockActual())
                .mensaje(alert.getMensaje())
                .fechaAlerta(alert.getFechaAlerta())
                .activa(alert.getActiva())
                .notificada(alert.getNotificada())
                .build();
    }
}
