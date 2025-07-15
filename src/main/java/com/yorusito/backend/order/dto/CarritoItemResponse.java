package com.yorusito.backend.order.dto;

import com.yorusito.backend.product.dto.ProductoResponse;
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
public class CarritoItemResponse {
    private Long id;
    private ProductoResponse producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private LocalDateTime fechaAgregado;
}
