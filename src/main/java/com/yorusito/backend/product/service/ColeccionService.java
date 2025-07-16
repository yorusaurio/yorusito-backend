package com.yorusito.backend.product.service;

import com.yorusito.backend.product.dto.ColeccionRequest;
import com.yorusito.backend.product.dto.ColeccionResponse;
import com.yorusito.backend.product.entity.Coleccion;
import com.yorusito.backend.product.repository.ColeccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ColeccionService {
    
    private final ColeccionRepository coleccionRepository;
    
    /**
     * Listar colecciones activas
     */
    @Transactional(readOnly = true)
    public List<ColeccionResponse> listarColeccionesActivas() {
        List<Coleccion> colecciones = coleccionRepository.findByActivaTrueOrderByFechaCreacionDesc();
        return colecciones.stream()
                .map(this::mapearAColeccionResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar colecciones destacadas
     */
    @Transactional(readOnly = true)
    public List<ColeccionResponse> listarColeccionesDestacadas() {
        List<Coleccion> colecciones = coleccionRepository.findByDestacadaTrueAndActivaTrueOrderByFechaCreacionDesc();
        return colecciones.stream()
                .map(this::mapearAColeccionResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar todas las colecciones paginadas (para admin)
     */
    @Transactional(readOnly = true)
    public Page<ColeccionResponse> listarTodasPaginadas(Pageable pageable) {
        Page<Coleccion> colecciones = coleccionRepository.findAll(pageable);
        return colecciones.map(this::mapearAColeccionResponse);
    }
    
    /**
     * Obtener colección por ID
     */
    @Transactional(readOnly = true)
    public ColeccionResponse obtenerPorId(Long id) {
        Coleccion coleccion = coleccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colección no encontrada"));
        return mapearAColeccionResponse(coleccion);
    }
    
    /**
     * Crear nueva colección
     */
    public ColeccionResponse crear(ColeccionRequest request) {
        // Validar que no exista una colección con el mismo nombre
        if (coleccionRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una colección con ese nombre");
        }
        
        Coleccion coleccion = Coleccion.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .imagenUrl(request.getImagenUrl())
                .activa(request.getActiva())
                .destacada(request.getDestacada())
                .temporada(request.getTemporada())
                .colorTematico(request.getColorTematico())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        coleccion = coleccionRepository.save(coleccion);
        log.info("Colección creada: {}", coleccion.getNombre());
        
        return mapearAColeccionResponse(coleccion);
    }
    
    /**
     * Actualizar colección
     */
    public ColeccionResponse actualizar(Long id, ColeccionRequest request) {
        Coleccion coleccion = coleccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colección no encontrada"));
        
        // Validar que no exista otra colección con el mismo nombre
        if (coleccionRepository.existsByNombreAndIdNot(request.getNombre(), id)) {
            throw new RuntimeException("Ya existe otra colección con ese nombre");
        }
        
        coleccion.setNombre(request.getNombre());
        coleccion.setDescripcion(request.getDescripcion());
        coleccion.setImagenUrl(request.getImagenUrl());
        coleccion.setActiva(request.getActiva());
        coleccion.setDestacada(request.getDestacada());
        coleccion.setTemporada(request.getTemporada());
        coleccion.setColorTematico(request.getColorTematico());
        coleccion.setFechaInicio(request.getFechaInicio());
        coleccion.setFechaFin(request.getFechaFin());
        coleccion.setFechaActualizacion(LocalDateTime.now());
        
        coleccion = coleccionRepository.save(coleccion);
        log.info("Colección actualizada: {}", coleccion.getNombre());
        
        return mapearAColeccionResponse(coleccion);
    }
    
    /**
     * Eliminar colección
     */
    public void eliminar(Long id) {
        Coleccion coleccion = coleccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colección no encontrada"));
        
        // Verificar si tiene productos asociados
        if (coleccion.getProductos() != null && !coleccion.getProductos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la colección porque tiene productos asociados");
        }
        
        coleccionRepository.delete(coleccion);
        log.info("Colección eliminada: {}", coleccion.getNombre());
    }
    
    /**
     * Activar/Desactivar colección
     */
    public ColeccionResponse toggleActiva(Long id) {
        Coleccion coleccion = coleccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colección no encontrada"));
        
        coleccion.setActiva(!coleccion.getActiva());
        coleccion.setFechaActualizacion(LocalDateTime.now());
        
        coleccion = coleccionRepository.save(coleccion);
        log.info("Colección {} {}", coleccion.getNombre(), coleccion.getActiva() ? "activada" : "desactivada");
        
        return mapearAColeccionResponse(coleccion);
    }
    
    /**
     * Destacar/No destacar colección
     */
    public ColeccionResponse toggleDestacada(Long id) {
        Coleccion coleccion = coleccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colección no encontrada"));
        
        coleccion.setDestacada(!coleccion.getDestacada());
        coleccion.setFechaActualizacion(LocalDateTime.now());
        
        coleccion = coleccionRepository.save(coleccion);
        log.info("Colección {} {}", coleccion.getNombre(), coleccion.getDestacada() ? "destacada" : "no destacada");
        
        return mapearAColeccionResponse(coleccion);
    }
    
    /**
     * Mapear entidad a DTO
     */
    private ColeccionResponse mapearAColeccionResponse(Coleccion coleccion) {
        return ColeccionResponse.builder()
                .id(coleccion.getId())
                .nombre(coleccion.getNombre())
                .descripcion(coleccion.getDescripcion())
                .imagenUrl(coleccion.getImagenUrl())
                .activa(coleccion.getActiva())
                .destacada(coleccion.getDestacada())
                .temporada(coleccion.getTemporada())
                .colorTematico(coleccion.getColorTematico())
                .fechaInicio(coleccion.getFechaInicio())
                .fechaFin(coleccion.getFechaFin())
                .fechaCreacion(coleccion.getFechaCreacion())
                .fechaActualizacion(coleccion.getFechaActualizacion())
                .cantidadProductos(coleccion.getProductos() != null ? coleccion.getProductos().size() : 0)
                .build();
    }
}
