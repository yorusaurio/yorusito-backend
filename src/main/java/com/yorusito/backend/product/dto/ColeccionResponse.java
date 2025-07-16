package com.yorusito.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColeccionResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private Boolean activa;
    private Boolean destacada;
    private String temporada;
    private String colorTematico;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer cantidadProductos;
}
