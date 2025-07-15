package com.yorusito.backend.notification.dto;

import com.yorusito.backend.shared.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    
    private Long id;
    private String titulo;
    private String mensaje;
    private NotificationType tipo;
    private Boolean leida;
    private Boolean enviada;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaLectura;
    private Long referenciaId;
    private String referenciaTipo;
}
