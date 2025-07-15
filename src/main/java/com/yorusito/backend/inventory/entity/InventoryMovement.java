package com.yorusito.backend.inventory.entity;

import com.yorusito.backend.product.entity.Producto;
import com.yorusito.backend.shared.enums.InventoryMovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryMovementType tipo;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "stock_anterior")
    private Integer stockAnterior;
    
    @Column(name = "stock_actual")
    private Integer stockActual;
    
    @Column(name = "motivo")
    private String motivo;
    
    @Column(name = "referencia_pedido")
    private String referenciaPedido;
    
    @Column(name = "fecha_movimiento")
    private LocalDateTime fechaMovimiento;
    
    @Column(name = "usuario_responsable")
    private String usuarioResponsable;
    
    @PrePersist
    protected void onCreate() {
        fechaMovimiento = LocalDateTime.now();
    }
}
