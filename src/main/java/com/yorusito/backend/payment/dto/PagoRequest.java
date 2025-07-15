package com.yorusito.backend.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {
    
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;
    
    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Size(min = 13, max = 19, message = "El número de tarjeta debe tener entre 13 y 19 dígitos")
    private String numeroTarjeta;
    
    @NotBlank(message = "El CVV es obligatorio")
    @Size(min = 3, max = 4, message = "El CVV debe tener entre 3 y 4 dígitos")
    private String cvv;
    
    @NotBlank(message = "El mes de expiración es obligatorio")
    @Size(min = 2, max = 2, message = "El mes de expiración debe tener 2 dígitos")
    private String mesExpiracion;
    
    @NotBlank(message = "El año de expiración es obligatorio")
    @Size(min = 4, max = 4, message = "El año de expiración debe tener 4 dígitos")
    private String anioExpiracion;
    
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String emailComprador;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    @Builder.Default
    private String moneda = "PEN";
}
