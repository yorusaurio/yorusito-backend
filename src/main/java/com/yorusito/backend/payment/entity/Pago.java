package com.yorusito.backend.payment.entity;

import com.yorusito.backend.order.entity.Pedido;
import com.yorusito.backend.shared.enums.EstadoPago;
import com.yorusito.backend.shared.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @Column(name = "culqi_charge_id", unique = true)
    private String culqiChargeId;
    
    @Column(name = "culqi_token_id")
    private String culqiTokenId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago;
    
    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;
    
    @Column(name = "moneda", length = 3)
    @Builder.Default
    private String moneda = "PEN";
    
    @Column(name = "email_comprador")
    private String emailComprador;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "detalles_respuesta", columnDefinition = "TEXT")
    private String detallesRespuesta;
    
    @Column(name = "mensaje_error")
    private String mensajeError;
    
    @Column(name = "numero_tarjeta_enmascarada")
    private String numeroTarjetaEnmascarada;
    
    @Column(name = "marca_tarjeta")
    private String marcaTarjeta;
    
    @Column(name = "exitoso")
    @Builder.Default
    private Boolean exitoso = false;
    
    @Column(name = "mensaje")
    private String mensaje;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
