package com.yorusito.backend.order.controller;

import com.yorusito.backend.order.dto.PedidoRequest;
import com.yorusito.backend.order.dto.PedidoResponse;
import com.yorusito.backend.order.service.PedidoService;
import com.yorusito.backend.shared.enums.EstadoPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Crear pedido", description = "Crea un nuevo pedido a partir del carrito del usuario")
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(request));
    }

    @GetMapping
    @Operation(summary = "Obtener pedidos del usuario", description = "Lista los pedidos del usuario autenticado")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosDelUsuario() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosDelUsuario());
    }

    @GetMapping("/paginado")
    @Operation(summary = "Obtener pedidos paginados", description = "Lista los pedidos del usuario con paginación")
    public ResponseEntity<Page<PedidoResponse>> obtenerPedidosDelUsuarioPaginado(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosDelUsuarioPaginado(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID", description = "Obtiene un pedido específico del usuario")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(id));
    }

    // Endpoints para administradores
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todos los pedidos", description = "Lista todos los pedidos (solo administradores)")
    public ResponseEntity<Page<PedidoResponse>> obtenerTodosPedidos(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.obtenerTodosPedidos(pageable));
    }

    @PutMapping("/admin/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar estado del pedido", description = "Actualiza el estado de un pedido (solo administradores)")
    public ResponseEntity<PedidoResponse> actualizarEstadoPedido(
            @PathVariable Long id, 
            @RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.actualizarEstadoPedido(id, estado));
    }
}
