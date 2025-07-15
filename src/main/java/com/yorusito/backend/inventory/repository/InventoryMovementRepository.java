package com.yorusito.backend.inventory.repository;

import com.yorusito.backend.inventory.entity.InventoryMovement;
import com.yorusito.backend.shared.enums.InventoryMovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    
    Page<InventoryMovement> findByProductoIdOrderByFechaMovimientoDesc(Long productoId, Pageable pageable);
    
    List<InventoryMovement> findByProductoIdAndTipoOrderByFechaMovimientoDesc(Long productoId, InventoryMovementType tipo);
    
    @Query("SELECT im FROM InventoryMovement im WHERE im.fechaMovimiento BETWEEN :startDate AND :endDate ORDER BY im.fechaMovimiento DESC")
    List<InventoryMovement> findByFechaMovimientoBetween(@Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT im FROM InventoryMovement im WHERE im.producto.id = :productoId AND im.fechaMovimiento BETWEEN :startDate AND :endDate ORDER BY im.fechaMovimiento DESC")
    List<InventoryMovement> findByProductoIdAndFechaMovimientoBetween(@Param("productoId") Long productoId,
                                                                    @Param("startDate") LocalDateTime startDate,
                                                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(CASE WHEN im.tipo = 'ENTRADA' THEN im.cantidad ELSE -im.cantidad END) " +
           "FROM InventoryMovement im WHERE im.producto.id = :productoId")
    Integer calculateCurrentStock(@Param("productoId") Long productoId);
}
