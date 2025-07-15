package com.yorusito.backend.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockResponse {
    
    private Long productoId;
    private String nombreProducto;
    private Integer stockActual;
    private Integer stockMinimo;
    private Boolean stockBajo;
    private Boolean stockAgotado;
    private String categoria;
}
