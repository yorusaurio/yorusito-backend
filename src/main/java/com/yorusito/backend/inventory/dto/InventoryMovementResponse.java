package com.yorusito.backend.inventory.dto;

import com.yorusito.backend.shared.enums.InventoryMovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementResponse {
    
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private InventoryMovementType tipo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockActual;
    private String motivo;
    private String referenciaPedido;
    private LocalDateTime fechaMovimiento;
    private String usuarioResponsable;
}
