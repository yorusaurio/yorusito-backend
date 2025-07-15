package com.yorusito.backend.notification.entity;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.shared.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String mensaje;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType tipo;
    
    @Column(name = "leida")
    @Builder.Default
    private Boolean leida = false;
    
    @Column(name = "enviada")
    @Builder.Default
    private Boolean enviada = false;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
    
    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;
    
    @Column(name = "referencia_id")
    private Long referenciaId; // ID del pedido, producto, etc.
    
    @Column(name = "referencia_tipo")
    private String referenciaTipo; // "ORDER", "PRODUCT", etc.
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
