package com.yorusito.backend.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColeccionRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    private String imagenUrl;
    
    @Builder.Default
    private Boolean activa = true;
    
    @Builder.Default
    private Boolean destacada = false;
    
    @Size(max = 50, message = "La temporada no puede exceder 50 caracteres")
    private String temporada;
    
    @Size(max = 7, message = "El color temático debe ser un color hex válido")
    private String colorTematico;
    
    private LocalDateTime fechaInicio;
    
    private LocalDateTime fechaFin;
}
