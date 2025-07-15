package com.yorusito.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccionEnvio;
    private String ciudad;
    private String codigoPostal;
    private String pais;
    private String telefonoAdicional;
    private LocalDateTime fechaNacimiento;
    private Boolean preferenciasNotificaciones;
    private Boolean aceptaMarketing;
    private LocalDateTime fechaActualizacion;
}
