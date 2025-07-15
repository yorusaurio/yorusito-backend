package com.yorusito.backend.user.entity;

import com.yorusito.backend.auth.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "direccion_envio")
    private String direccionEnvio;
    
    @Column(name = "ciudad")
    private String ciudad;
    
    @Column(name = "codigo_postal")
    private String codigoPostal;
    
    @Column(name = "pais")
    private String pais;
    
    @Column(name = "telefono_adicional")
    private String telefonoAdicional;
    
    @Column(name = "fecha_nacimiento")
    private LocalDateTime fechaNacimiento;
    
    @Column(name = "preferencias_notificaciones")
    @Builder.Default
    private Boolean preferenciasNotificaciones = true;
    
    @Column(name = "acepta_marketing")
    @Builder.Default
    private Boolean aceptaMarketing = false;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
