package com.yorusito.backend.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {
    
    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccionEnvio;
    
    @NotBlank(message = "El teléfono de contacto es obligatorio")
    private String telefonoContacto;
    
    private String observaciones;
}
