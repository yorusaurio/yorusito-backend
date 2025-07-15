package com.yorusito.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    
    private Long totalProductos;
    private Long totalUsuarios;
    private Long totalPedidos;
    private BigDecimal ventasHoy;
    private BigDecimal ventasSemana;
    private BigDecimal ventasMes;
    private Long productosStockBajo;
    private Long pedidosPendientes;
    private Long reviewsPendientes;
    private Double ratingPromedio;
}
