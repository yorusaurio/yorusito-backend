package com.yorusito.backend.product.service;

import com.yorusito.backend.product.dto.CategoriaResponse;
import com.yorusito.backend.product.dto.ProductoRequest;
import com.yorusito.backend.product.dto.ProductoResponse;
import com.yorusito.backend.product.entity.Categoria;
import com.yorusito.backend.product.entity.Producto;
import com.yorusito.backend.product.repository.CategoriaRepository;
import com.yorusito.backend.product.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public List<ProductoResponse> obtenerTodos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public Page<ProductoResponse> obtenerTodosPaginado(Pageable pageable) {
        return productoRepository.findByActivoTrue(pageable)
                .map(this::convertirAResponse);
    }

    public Page<ProductoResponse> buscarProductos(String search, Pageable pageable) {
        return productoRepository.findBySearchAndActivoTrue(search, pageable)
                .map(this::convertirAResponse);
    }

    public List<ProductoResponse> obtenerPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertirAResponse(producto);
    }

    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .imagenUrl(request.getImagenUrl())
                .categoria(categoria)
                .build();

        producto = productoRepository.save(producto);
        return convertirAResponse(producto);
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setCategoria(categoria);

        producto = productoRepository.save(producto);
        return convertirAResponse(producto);
    }

    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private ProductoResponse convertirAResponse(Producto producto) {
        CategoriaResponse categoriaResponse = CategoriaResponse.builder()
                .id(producto.getCategoria().getId())
                .nombre(producto.getCategoria().getNombre())
                .descripcion(producto.getCategoria().getDescripcion())
                .fechaCreacion(producto.getCategoria().getFechaCreacion())
                .build();

        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .imagenUrl(producto.getImagenUrl())
                .categoria(categoriaResponse)
                .activo(producto.getActivo())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaActualizacion(producto.getFechaActualizacion())
                .build();
    }
}
