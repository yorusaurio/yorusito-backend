package com.yorusito.backend.notification.service;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.auth.repository.UsuarioRepository;
import com.yorusito.backend.notification.dto.NotificationResponse;
import com.yorusito.backend.notification.entity.Notification;
import com.yorusito.backend.notification.repository.NotificationRepository;
import com.yorusito.backend.shared.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public Notification createNotification(Usuario usuario, String titulo, String mensaje, 
                                         NotificationType tipo, Long referenciaId, String referenciaTipo) {
        Notification notification = Notification.builder()
                .usuario(usuario)
                .titulo(titulo)
                .mensaje(mensaje)
                .tipo(tipo)
                .referenciaId(referenciaId)
                .referenciaTipo(referenciaTipo)
                .build();
        
        notification = notificationRepository.save(notification);
        
        // Aquí se podría integrar con un servicio de email, SMS, etc.
        sendNotification(notification);
        
        return notification;
    }
    
    private void sendNotification(Notification notification) {
        try {
            // Simular envío de notificación
            log.info("Enviando notificación: {} a usuario: {}", 
                    notification.getTitulo(), notification.getUsuario().getEmail());
            
            // Marcar como enviada
            notification.setEnviada(true);
            notification.setFechaEnvio(LocalDateTime.now());
            notificationRepository.save(notification);
            
        } catch (Exception e) {
            log.error("Error enviando notificación: {}", e.getMessage());
        }
    }
    
    @Transactional
    public void markAsRead(Long notificationId, Long usuarioId) {
        notificationRepository.findByIdAndUsuarioId(notificationId, usuarioId)
                .ifPresent(notification -> {
                    notification.setLeida(true);
                    notification.setFechaLectura(LocalDateTime.now());
                    notificationRepository.save(notification);
                });
    }
    
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(String email, Pageable pageable) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        Page<Notification> notifications = notificationRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuario.getId(), pageable);
        return notifications.map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        List<Notification> notifications = notificationRepository.findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuario.getId());
        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Long getUnreadCount(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        return notificationRepository.countByUsuarioIdAndLeidaFalse(usuario.getId());
    }
    
    @Transactional
    public void markAsRead(String email, Long notificationId) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        markAsRead(notificationId, usuario.getId());
    }
    
    @Transactional
    public void markAllAsRead(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        List<Notification> unreadNotifications = notificationRepository.findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuario.getId());
        
        unreadNotifications.forEach(notification -> {
            notification.setLeida(true);
            notification.setFechaLectura(LocalDateTime.now());
        });
        
        notificationRepository.saveAll(unreadNotifications);
    }
    
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .titulo(notification.getTitulo())
                .mensaje(notification.getMensaje())
                .tipo(notification.getTipo())
                .leida(notification.getLeida())
                .enviada(notification.getEnviada())
                .fechaCreacion(notification.getFechaCreacion())
                .fechaEnvio(notification.getFechaEnvio())
                .fechaLectura(notification.getFechaLectura())
                .referenciaId(notification.getReferenciaId())
                .referenciaTipo(notification.getReferenciaTipo())
                .build();
    }
}
