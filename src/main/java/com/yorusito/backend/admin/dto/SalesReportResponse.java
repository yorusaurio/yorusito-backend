package com.yorusito.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportResponse {
    
    private LocalDate fecha;
    private Long totalPedidos;
    private BigDecimal totalVentas;
    private BigDecimal ventasPromedio;
    private Long clientesUnicos;
    private Long productosVendidos;
    private String productoMasVendido;
    private Long cantidadProductoMasVendido;
}
