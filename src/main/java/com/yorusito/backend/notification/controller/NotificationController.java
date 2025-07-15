package com.yorusito.backend.notification.controller;

import com.yorusito.backend.notification.dto.NotificationResponse;
import com.yorusito.backend.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Gestión de notificaciones de usuario")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    @Operation(summary = "Obtener notificaciones", description = "Obtiene las notificaciones del usuario autenticado")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(authentication.getName(), pageable);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/unread")
    @Operation(summary = "Obtener notificaciones no leídas", description = "Obtiene solo las notificaciones no leídas")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(Authentication authentication) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(authentication.getName());
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/count/unread")
    @Operation(summary = "Contar notificaciones no leídas", description = "Obtiene el número de notificaciones no leídas")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        Long count = notificationService.getUnreadCount(authentication.getName());
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Marcar como leída", description = "Marca una notificación como leída")
    public ResponseEntity<Void> markAsRead(
            Authentication authentication,
            @PathVariable Long notificationId) {
        notificationService.markAsRead(authentication.getName(), notificationId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/read-all")
    @Operation(summary = "Marcar todas como leídas", description = "Marca todas las notificaciones como leídas")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.ok().build();
    }
}
