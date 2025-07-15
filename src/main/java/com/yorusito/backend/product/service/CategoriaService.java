package com.yorusito.backend.product.service;

import com.yorusito.backend.product.dto.CategoriaRequest;
import com.yorusito.backend.product.dto.CategoriaResponse;
import com.yorusito.backend.product.entity.Categoria;
import com.yorusito.backend.product.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponse> obtenerTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public CategoriaResponse obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return convertirAResponse(categoria);
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();

        categoria = categoriaRepository.save(categoria);
        return convertirAResponse(categoria);
    }

    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (!categoria.getNombre().equals(request.getNombre()) && 
            categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        categoria = categoriaRepository.save(categoria);
        return convertirAResponse(categoria);
    }

    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    private CategoriaResponse convertirAResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .fechaCreacion(categoria.getFechaCreacion())
                .totalProductos(categoria.getProductos() != null ? categoria.getProductos().size() : 0)
                .build();
    }
}
