package com.yorusito.backend.order.controller;

import com.yorusito.backend.order.dto.CarritoItemResponse;
import com.yorusito.backend.order.dto.CarritoRequest;
import com.yorusito.backend.order.dto.CarritoResponse;
import com.yorusito.backend.order.service.CarritoService;
import com.yorusito.backend.whatsapp.dto.WhatsAppResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito de Compras", description = "Gestión del carrito de compras del usuario")
@SecurityRequirement(name = "bearerAuth")
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    @Operation(summary = "Obtener carrito", description = "Obtiene el carrito del usuario autenticado")
    public ResponseEntity<CarritoResponse> obtenerCarrito() {
        return ResponseEntity.ok(carritoService.obtenerCarrito());
    }

    @PostMapping("/agregar")
    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito del usuario")
    public ResponseEntity<CarritoItemResponse> agregarProducto(@Valid @RequestBody CarritoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.agregarProducto(request));
    }

    @DeleteMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar producto del carrito", description = "Elimina un producto específico del carrito")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        carritoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/vaciar")
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos del carrito")
    public ResponseEntity<Void> vaciarCarrito() {
        carritoService.vaciarCarrito();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/whatsapp")
    @Operation(summary = "Enviar a WhatsApp", description = "Genera un enlace de WhatsApp con el contenido del carrito")
    public ResponseEntity<WhatsAppResponseDTO> enviarCarritoAWhatsApp() {
        try {
            WhatsAppResponseDTO response = carritoService.enviarCarritoAWhatsApp();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            WhatsAppResponseDTO errorResponse = WhatsAppResponseDTO.builder()
                    .mensaje(e.getMessage())
                    .exitoso(false)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
