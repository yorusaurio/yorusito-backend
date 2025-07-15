package com.yorusito.backend.payment.dto;

import com.yorusito.backend.shared.enums.EstadoPago;
import com.yorusito.backend.shared.enums.MetodoPago;
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
public class PagoResponse {
    
    private Long id;
    private Long pedidoId;
    private String culqiChargeId;
    private EstadoPago estado;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private String moneda;
    private String emailComprador;
    private String descripcion;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaCreacion;
    private String mensaje;
    private String numeroTarjetaEnmascarada;
    private String marcaTarjeta;
    private Boolean exitoso;
}
