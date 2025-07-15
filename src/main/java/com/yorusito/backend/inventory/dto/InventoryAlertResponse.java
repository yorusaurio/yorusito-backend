package com.yorusito.backend.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlertResponse {
    
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer stockMinimo;
    private Integer stockActual;
    private String mensaje;
    private LocalDateTime fechaAlerta;
    private Boolean activa;
    private Boolean notificada;
}
