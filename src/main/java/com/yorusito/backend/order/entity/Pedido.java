package com.yorusito.backend.order.entity;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.shared.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoItem> items;
    
    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    
    @Column(name = "direccion_envio", columnDefinition = "TEXT")
    private String direccionEnvio;
    
    @Column(name = "telefono_contacto")
    private String telefonoContacto;
    
    private String observaciones;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaPedido = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
