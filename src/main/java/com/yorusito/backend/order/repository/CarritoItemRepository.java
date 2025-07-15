package com.yorusito.backend.order.repository;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.order.entity.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByUsuarioId(Long usuarioId);
    List<CarritoItem> findByUsuarioOrderByFechaAgregadoDesc(Usuario usuario);
    Optional<CarritoItem> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
    
    @Modifying
    @Query("DELETE FROM CarritoItem c WHERE c.usuario.id = :usuarioId")
    void deleteAllByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT SUM(c.subtotal) FROM CarritoItem c WHERE c.usuario.id = :usuarioId")
    Double getTotalByUsuarioId(@Param("usuarioId") Long usuarioId);
}
