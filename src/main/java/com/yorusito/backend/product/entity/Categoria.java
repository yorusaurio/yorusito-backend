package com.yorusito.backend.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categorias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    private String descripcion;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
