package com.yorusito.backend.order.service;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.order.dto.*;
import com.yorusito.backend.order.entity.CarritoItem;
import com.yorusito.backend.order.entity.Pedido;
import com.yorusito.backend.order.entity.PedidoItem;
import com.yorusito.backend.order.repository.PedidoRepository;
import com.yorusito.backend.product.dto.CategoriaResponse;
import com.yorusito.backend.product.dto.ProductoResponse;
import com.yorusito.backend.product.entity.Producto;
import com.yorusito.backend.product.repository.ProductoRepository;
import com.yorusito.backend.shared.enums.EstadoPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoService carritoService;
    private final ProductoRepository productoRepository;

    public PedidoResponse crearPedido(PedidoRequest request) {
        Usuario usuario = getUsuarioAutenticado();
        
        // Obtener items del carrito
        List<CarritoItem> carritoItems = carritoService.obtenerItemsDelCarrito(usuario.getId());
        
        if (carritoItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Verificar stock disponible
        for (CarritoItem item : carritoItems) {
            Producto producto = item.getProducto();
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }
        }

        // Crear el pedido
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .direccionEnvio(request.getDireccionEnvio())
                .telefonoContacto(request.getTelefonoContacto())
                .observaciones(request.getObservaciones())
                .build();

        // Crear los items del pedido
        List<PedidoItem> pedidoItems = carritoItems.stream()
                .map(carritoItem -> PedidoItem.builder()
                        .pedido(pedido)
                        .producto(carritoItem.getProducto())
                        .cantidad(carritoItem.getCantidad())
                        .precioUnitario(carritoItem.getPrecioUnitario())
                        .build())
                .collect(Collectors.toList());

        pedido.setItems(pedidoItems);

        // Calcular total
        BigDecimal total = pedidoItems.stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        pedido.setTotal(total);

        // Guardar pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Actualizar stock de productos
        for (CarritoItem item : carritoItems) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        // Vaciar carrito
        carritoService.vaciarCarrito();

        return convertirAPedidoResponse(pedidoGuardado);
    }

    public List<PedidoResponse> obtenerPedidosDelUsuario() {
        Usuario usuario = getUsuarioAutenticado();
        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuario.getId());
        return pedidos.stream()
                .map(this::convertirAPedidoResponse)
                .collect(Collectors.toList());
    }

    public Page<PedidoResponse> obtenerPedidosDelUsuarioPaginado(Pageable pageable) {
        Usuario usuario = getUsuarioAutenticado();
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuario.getId(), pageable)
                .map(this::convertirAPedidoResponse);
    }

    public PedidoResponse obtenerPedidoPorId(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para ver este pedido");
        }

        return convertirAPedidoResponse(pedido);
    }

    // Métodos para administradores
    public Page<PedidoResponse> obtenerTodosPedidos(Pageable pageable) {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc(pageable)
                .map(this::convertirAPedidoResponse);
    }

    public PedidoResponse actualizarEstadoPedido(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        pedido = pedidoRepository.save(pedido);

        return convertirAPedidoResponse(pedido);
    }

    private Usuario getUsuarioAutenticado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private PedidoResponse convertirAPedidoResponse(Pedido pedido) {
        List<PedidoItemResponse> itemResponses = pedido.getItems().stream()
                .map(this::convertirAPedidoItemResponse)
                .collect(Collectors.toList());

        return PedidoResponse.builder()
                .id(pedido.getId())
                .items(itemResponses)
                .fechaPedido(pedido.getFechaPedido())
                .total(pedido.getTotal())
                .estado(pedido.getEstado())
                .direccionEnvio(pedido.getDireccionEnvio())
                .telefonoContacto(pedido.getTelefonoContacto())
                .observaciones(pedido.getObservaciones())
                .fechaActualizacion(pedido.getFechaActualizacion())
                .build();
    }

    private PedidoItemResponse convertirAPedidoItemResponse(PedidoItem item) {
        ProductoResponse productoResponse = convertirAProductoResponse(item.getProducto());

        return PedidoItemResponse.builder()
                .id(item.getId())
                .producto(productoResponse)
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
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
}
