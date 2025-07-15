package com.yorusito.backend.order.dto;

import com.yorusito.backend.shared.enums.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    private Long id;
    private List<PedidoItemResponse> items;
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private EstadoPedido estado;
    private String direccionEnvio;
    private String telefonoContacto;
    private String observaciones;
    private LocalDateTime fechaActualizacion;
}
