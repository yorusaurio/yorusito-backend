package com.yorusito.backend.inventory.entity;

import com.yorusito.backend.product.entity.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(name = "stock_minimo")
    private Integer stockMinimo;
    
    @Column(name = "stock_actual")
    private Integer stockActual;
    
    @Column(name = "mensaje")
    private String mensaje;
    
    @Column(name = "fecha_alerta")
    private LocalDateTime fechaAlerta;
    
    @Builder.Default
    private Boolean activa = true;
    
    @Builder.Default
    private Boolean notificada = false;
    
    @PrePersist
    protected void onCreate() {
        fechaAlerta = LocalDateTime.now();
    }
}
