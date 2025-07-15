package com.yorusito.backend.notification.repository;

import com.yorusito.backend.notification.entity.Notification;
import com.yorusito.backend.shared.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId, Pageable pageable);
    
    List<Notification> findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(Long usuarioId);
    
    Long countByUsuarioIdAndLeidaFalse(Long usuarioId);
    
    Optional<Notification> findByIdAndUsuarioId(Long id, Long usuarioId);
    
    List<Notification> findByUsuarioIdAndTipoOrderByFechaCreacionDesc(Long usuarioId, NotificationType tipo);
    
    @Query("SELECT n FROM Notification n WHERE n.usuario.id = :usuarioId AND n.enviada = false")
    List<Notification> findPendingNotifications(@Param("usuarioId") Long usuarioId);
    
    void deleteByUsuarioIdAndLeidaTrueAndFechaCreacionBefore(Long usuarioId, java.time.LocalDateTime fecha);
}
