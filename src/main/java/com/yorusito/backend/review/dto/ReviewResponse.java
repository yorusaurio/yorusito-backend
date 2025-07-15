package com.yorusito.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private String nombreUsuario;
    private String comment;
    private Integer rating;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
