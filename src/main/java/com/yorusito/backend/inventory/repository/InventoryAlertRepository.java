package com.yorusito.backend.inventory.repository;

import com.yorusito.backend.inventory.entity.InventoryAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryAlertRepository extends JpaRepository<InventoryAlert, Long> {
    
    List<InventoryAlert> findByActivaTrueOrderByFechaAlertaDesc();
    
    List<InventoryAlert> findByActivaTrueAndNotificadaFalseOrderByFechaAlertaDesc();
    
    Optional<InventoryAlert> findByProductoIdAndActivaTrue(Long productoId);
    
    @Query("SELECT ia FROM InventoryAlert ia WHERE ia.producto.id = :productoId AND ia.activa = true")
    List<InventoryAlert> findActiveAlertsByProductoId(@Param("productoId") Long productoId);
    
    void deleteByProductoIdAndActivaTrue(Long productoId);
}
