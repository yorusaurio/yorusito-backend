package com.yorusito.backend.admin.service;

import com.yorusito.backend.admin.dto.DashboardStats;
import com.yorusito.backend.admin.dto.SalesReportResponse;
import com.yorusito.backend.auth.repository.UsuarioRepository;
import com.yorusito.backend.inventory.repository.InventoryAlertRepository;
import com.yorusito.backend.order.repository.OrderRepository;
import com.yorusito.backend.product.repository.ProductoRepository;
import com.yorusito.backend.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final InventoryAlertRepository inventoryAlertRepository;
    
    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = inicioHoy.plusDays(1);
        
        LocalDateTime inicioSemana = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime inicioMes = LocalDate.now().minusDays(30).atStartOfDay();
        
        return DashboardStats.builder()
                .totalProductos(productoRepository.count())
                .totalUsuarios(usuarioRepository.count())
                .totalPedidos(orderRepository.count())
                .ventasHoy(getVentasPorPeriodo(inicioHoy, finHoy))
                .ventasSemana(getVentasPorPeriodo(inicioSemana, finHoy))
                .ventasMes(getVentasPorPeriodo(inicioMes, finHoy))
                .productosStockBajo(getProductosStockBajo())
                .pedidosPendientes(getPedidosPendientes())
                .reviewsPendientes(reviewRepository.count()) // Todas las reviews activas
                .ratingPromedio(getRatingPromedio())
                .build();
    }
    
    @Transactional(readOnly = true)
    public List<SalesReportResponse> getSalesReport(LocalDate fechaInicio, LocalDate fechaFin) {
        return fechaInicio.datesUntil(fechaFin.plusDays(1))
                .map(this::getSalesForDate)
                .collect(Collectors.toList());
    }
    
    private SalesReportResponse getSalesForDate(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        
        Long totalPedidos = orderRepository.countByFechaCreacionBetween(inicio, fin);
        BigDecimal totalVentas = orderRepository.sumTotalByFechaCreacionBetween(inicio, fin);
        
        return SalesReportResponse.builder()
                .fecha(fecha)
                .totalPedidos(totalPedidos != null ? totalPedidos : 0L)
                .totalVentas(totalVentas != null ? totalVentas : BigDecimal.ZERO)
                .ventasPromedio(totalPedidos != null && totalPedidos > 0 && totalVentas != null ? 
                               totalVentas.divide(BigDecimal.valueOf(totalPedidos), 2, java.math.RoundingMode.HALF_UP) : 
                               BigDecimal.ZERO)
                .clientesUnicos(0L) // Implementar cuando sea necesario
                .productosVendidos(0L) // Implementar cuando sea necesario
                .productoMasVendido("N/A") // Implementar cuando sea necesario
                .cantidadProductoMasVendido(0L) // Implementar cuando sea necesario
                .build();
    }
    
    private BigDecimal getVentasPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        BigDecimal ventas = orderRepository.sumTotalByFechaCreacionBetween(inicio, fin);
        return ventas != null ? ventas : BigDecimal.ZERO;
    }
    
    private Long getProductosStockBajo() {
        return productoRepository.countByStockLessThanEqual(10);
    }
    
    private Long getPedidosPendientes() {
        return orderRepository.countByEstado(com.yorusito.backend.shared.enums.OrderStatus.PENDIENTE);
    }
    
    private Double getRatingPromedio() {
        // Implementar consulta para rating promedio de todos los productos
        return 0.0;
    }
}
