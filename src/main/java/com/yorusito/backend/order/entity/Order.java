package com.yorusito.backend.order.entity;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.shared.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "numero_orden", unique = true, nullable = false)
    private String numeroOrden;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus estado = OrderStatus.PENDIENTE;
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "impuestos", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal impuestos = BigDecimal.ZERO;
    
    @Column(name = "costo_envio", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoEnvio = BigDecimal.ZERO;
    
    @Column(name = "direccion_envio", nullable = false)
    private String direccionEnvio;
    
    @Column(name = "ciudad_envio", nullable = false)
    private String ciudadEnvio;
    
    @Column(name = "codigo_postal_envio")
    private String codigoPostalEnvio;
    
    @Column(name = "pais_envio", nullable = false)
    private String paisEnvio;
    
    @Column(name = "telefono_contacto")
    private String telefonoContacto;
    
    @Column(name = "notas_especiales")
    private String notasEspeciales;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
    
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (numeroOrden == null) {
            numeroOrden = "ORD-" + System.currentTimeMillis();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
