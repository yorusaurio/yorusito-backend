package com.yorusito.backend.order.service;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.order.dto.*;
import com.yorusito.backend.order.entity.CarritoItem;
import com.yorusito.backend.order.repository.CarritoItemRepository;
import com.yorusito.backend.product.dto.CategoriaResponse;
import com.yorusito.backend.product.dto.ProductoResponse;
import com.yorusito.backend.product.entity.Producto;
import com.yorusito.backend.product.repository.ProductoRepository;
import com.yorusito.backend.whatsapp.dto.WhatsAppResponseDTO;
import com.yorusito.backend.whatsapp.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final WhatsAppService whatsAppService;

    public CarritoResponse obtenerCarrito() {
        Usuario usuario = getUsuarioAutenticado();
        List<CarritoItem> items = carritoItemRepository.findByUsuarioId(usuario.getId());
        
        List<CarritoItemResponse> itemResponses = items.stream()
                .map(this::convertirACarritoItemResponse)
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CarritoResponse.builder()
                .items(itemResponses)
                .total(total)
                .totalItems(items.size())
                .build();
    }

    public CarritoItemResponse agregarProducto(CarritoRequest request) {
        Usuario usuario = getUsuarioAutenticado();
        
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!producto.getActivo()) {
            throw new RuntimeException("El producto no está disponible");
        }

        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente");
        }

        // Verificar si el producto ya está en el carrito
        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findByUsuarioIdAndProductoId(usuario.getId(), producto.getId());

        CarritoItem carritoItem;
        if (itemExistente.isPresent()) {
            carritoItem = itemExistente.get();
            int nuevaCantidad = carritoItem.getCantidad() + request.getCantidad();
            
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente para la cantidad solicitada");
            }
            
            carritoItem.setCantidad(nuevaCantidad);
        } else {
            carritoItem = CarritoItem.builder()
                    .usuario(usuario)
                    .producto(producto)
                    .cantidad(request.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .build();
        }

        carritoItem = carritoItemRepository.save(carritoItem);
        return convertirACarritoItemResponse(carritoItem);
    }

    public void eliminarProducto(Long carritoItemId) {
        Usuario usuario = getUsuarioAutenticado();
        
        CarritoItem carritoItem = carritoItemRepository.findById(carritoItemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));

        if (!carritoItem.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este item");
        }

        carritoItemRepository.delete(carritoItem);
    }

    public void vaciarCarrito() {
        Usuario usuario = getUsuarioAutenticado();
        carritoItemRepository.deleteAllByUsuarioId(usuario.getId());
    }

    public List<CarritoItem> obtenerItemsDelCarrito(Long usuarioId) {
        return carritoItemRepository.findByUsuarioId(usuarioId);
    }

    private Usuario getUsuarioAutenticado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private CarritoItemResponse convertirACarritoItemResponse(CarritoItem item) {
        ProductoResponse productoResponse = convertirAProductoResponse(item.getProducto());
        
        return CarritoItemResponse.builder()
                .id(item.getId())
                .producto(productoResponse)
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .fechaAgregado(item.getFechaAgregado())
                .build();
    }

    private ProductoResponse convertirAProductoResponse(Producto producto) {
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
    
    /**
     * Enviar carrito a WhatsApp
     */
    public WhatsAppResponseDTO enviarCarritoAWhatsApp() {
        Usuario usuario = getUsuarioAutenticado();
        List<CarritoItem> items = carritoItemRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario);
        
        if (items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }
        
        // Generar enlace de WhatsApp
        String enlaceWhatsApp = whatsAppService.generarEnlaceWhatsApp(usuario, items);
        
        // Calcular total
        BigDecimal total = items.stream()
                .map(item -> item.getProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Preparar respuesta
        WhatsAppResponseDTO response = WhatsAppResponseDTO.builder()
                .enlaceWhatsApp(enlaceWhatsApp)
                .mensaje("Enlace generado exitosamente")
                .total(total)
                .cantidadItems(items.size())
                .fechaGeneracion(LocalDateTime.now())
                .numeroWhatsApp(whatsAppService.getNumeroWhatsApp())
                .exitoso(true)
                .build();
        
        return response;
    }
}
