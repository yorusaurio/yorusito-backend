package com.yorusito.backend.review.repository;

import com.yorusito.backend.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByProductoIdAndActivoTrueOrderByFechaCreacionDesc(Long productoId, Pageable pageable);
    
    Page<Review> findByUsuarioIdAndActivoTrueOrderByFechaCreacionDesc(Long usuarioId, Pageable pageable);
    
    boolean existsByProductoIdAndUsuarioIdAndActivoTrue(Long productoId, Long usuarioId);
    
    Optional<Review> findByIdAndUsuarioIdAndActivoTrue(Long id, Long usuarioId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.producto.id = :productoId AND r.activo = true")
    Double findAverageRatingByProductoId(@Param("productoId") Long productoId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.producto.id = :productoId AND r.activo = true")
    Long countByProductoId(@Param("productoId") Long productoId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.producto.id = :productoId AND r.rating = :rating AND r.activo = true")
    Long countByProductoIdAndRating(@Param("productoId") Long productoId, @Param("rating") Integer rating);
    
    @Query("SELECT r FROM Review r WHERE r.producto.id = :productoId AND r.activo = true ORDER BY r.fechaCreacion DESC")
    List<Review> findTop10ByProductoIdOrderByFechaCreacionDesc(@Param("productoId") Long productoId, Pageable pageable);
}
