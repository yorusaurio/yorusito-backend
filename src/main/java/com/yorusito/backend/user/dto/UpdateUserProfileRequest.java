package com.yorusito.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    private String telefono;
    private String direccionEnvio;
    private String ciudad;
    private String codigoPostal;
    private String pais;
    private String telefonoAdicional;
    private LocalDateTime fechaNacimiento;
    
    @NotNull(message = "Las preferencias de notificación son obligatorias")
    private Boolean preferenciasNotificaciones;
    
    @NotNull(message = "La aceptación de marketing es obligatoria")
    private Boolean aceptaMarketing;
}
