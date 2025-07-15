package com.yorusito.backend.product.repository;

import com.yorusito.backend.product.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);
    Page<Producto> findByActivoTrue(Pageable pageable);
    
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Producto> findBySearchAndActivoTrue(@Param("search") String search, Pageable pageable);
    
    Long countByStockLessThanEqual(Integer stock);
    
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "(:search IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
           "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
           "(:precioMax IS NULL OR p.precio <= :precioMax) AND " +
           "(:enStock IS NULL OR (:enStock = true AND p.stock > 0) OR (:enStock = false))")
    Page<Producto> busquedaAvanzada(@Param("search") String search,
                                   @Param("categoriaId") Long categoriaId,
                                   @Param("precioMin") java.math.BigDecimal precioMin,
                                   @Param("precioMax") java.math.BigDecimal precioMax,
                                   @Param("enStock") Boolean enStock,
                                   Pageable pageable);
    
    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.id DESC")
    List<Producto> findProductosPopulares(@Param("limit") int limit);
    
    List<Producto> findByCategoriaIdAndActivoTrueAndIdNot(Long categoriaId, Long productoId);
}
