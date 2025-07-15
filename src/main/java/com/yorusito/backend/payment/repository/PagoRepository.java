package com.yorusito.backend.payment.repository;

import com.yorusito.backend.payment.entity.Pago;
import com.yorusito.backend.shared.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    /**
     * Buscar pagos por ID de pedido
     */
    List<Pago> findByPedidoId(Long pedidoId);
    
    /**
     * Buscar pago por ID de cargo de Culqi
     */
    Optional<Pago> findByCulqiChargeId(String culqiChargeId);
    
    /**
     * Buscar pagos por estado
     */
    List<Pago> findByEstado(EstadoPago estado);
    
    /**
     * Buscar pagos exitosos de un usuario
     */
    @Query("SELECT p FROM Pago p WHERE p.pedido.usuario.id = :usuarioId AND p.exitoso = true")
    List<Pago> findPagosExitososByUsuario(@Param("usuarioId") Long usuarioId);
    
    /**
     * Buscar pagos pendientes que necesiten verificación
     */
    @Query("SELECT p FROM Pago p WHERE p.estado = :estado AND p.fechaCreacion < :fechaLimite")
    List<Pago> findPagosPendientesParaVerificar(@Param("estado") EstadoPago estado, @Param("fechaLimite") LocalDateTime fechaLimite);
    
    /**
     * Verificar si existe un pago exitoso para un pedido
     */
    boolean existsByPedidoIdAndExitosoTrue(Long pedidoId);
}
