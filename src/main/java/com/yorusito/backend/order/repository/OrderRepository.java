package com.yorusito.backend.order.repository;

import com.yorusito.backend.order.entity.Order;
import com.yorusito.backend.shared.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId, Pageable pageable);
    
    List<Order> findByUsuarioIdAndEstadoOrderByFechaCreacionDesc(Long usuarioId, OrderStatus estado);
    
    Optional<Order> findByNumeroOrden(String numeroOrden);
    
    List<Order> findByEstadoOrderByFechaCreacionDesc(OrderStatus estado);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.estado = :estado")
    Long countByEstado(@Param("estado") OrderStatus estado);
    
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.fechaCreacion BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalByFechaCreacionBetween(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.fechaCreacion BETWEEN :startDate AND :endDate")
    Long countByFechaCreacionBetween(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
}
