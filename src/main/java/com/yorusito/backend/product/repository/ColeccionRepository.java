package com.yorusito.backend.product.repository;

import com.yorusito.backend.product.entity.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColeccionRepository extends JpaRepository<Coleccion, Long> {
    
    /**
     * Buscar colección por nombre
     */
    Optional<Coleccion> findByNombre(String nombre);
    
    /**
     * Buscar colecciones activas
     */
    List<Coleccion> findByActivaTrueOrderByFechaCreacionDesc();
    
    /**
     * Buscar colecciones destacadas
     */
    List<Coleccion> findByDestacadaTrueAndActivaTrueOrderByFechaCreacionDesc();
    
    /**
     * Buscar colecciones por temporada
     */
    List<Coleccion> findByTemporadaAndActivaTrueOrderByFechaCreacionDesc(String temporada);
    
    /**
     * Verificar si existe una colección con el mismo nombre
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Verificar si existe una colección con el mismo nombre excluyendo un ID
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);
    
    /**
     * Obtener colecciones con cantidad de productos
     */
    @Query("SELECT c FROM Coleccion c LEFT JOIN FETCH c.productos WHERE c.activa = true ORDER BY c.fechaCreacion DESC")
    List<Coleccion> findColeccionesConProductos();
}
