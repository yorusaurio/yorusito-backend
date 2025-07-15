package com.yorusito.backend.order.entity;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.product.entity.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "carrito_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;
    
    @PrePersist
    protected void onCreate() {
        fechaAgregado = LocalDateTime.now();
        calcularSubtotal();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calcularSubtotal();
    }
    
    private void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
}
