package com.yorusito.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String imagenUrl;
    private CategoriaResponse categoria;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
