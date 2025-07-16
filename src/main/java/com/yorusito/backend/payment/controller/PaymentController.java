package com.yorusito.backend.payment.controller;

import com.yorusito.backend.payment.dto.PagoRequest;
import com.yorusito.backend.payment.dto.PagoResponse;
import com.yorusito.backend.payment.service.PaymentServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pagos", description = "API para gestión de pagos con Culqi")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    
    private final PaymentServiceInterface paymentService;
    
    @PostMapping("/procesar")
    @Operation(summary = "Procesar un pago con tarjeta", 
               description = "Procesa un pago usando la pasarela de Culqi")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago procesado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de pago inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PagoResponse> procesarPago(
            @Valid @RequestBody PagoRequest pagoRequest,
            Authentication authentication) {
        
        try {
            log.info("Procesando pago para pedido: {}", pagoRequest.getPedidoId());
            PagoResponse pagoResponse = paymentService.procesarPago(pagoRequest);
            
            return ResponseEntity.ok(pagoResponse);
            
        } catch (RuntimeException e) {
            log.error("Error procesando pago: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PagoResponse.builder()
                            .mensaje("Error: " + e.getMessage())
                            .exitoso(false)
                            .build());
        }
    }
    
    @GetMapping("/{pagoId}")
    @Operation(summary = "Obtener estado de un pago", 
               description = "Consulta el estado actual de un pago")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del pago obtenido"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<PagoResponse> obtenerEstadoPago(
            @Parameter(description = "ID del pago") @PathVariable Long pagoId) {
        
        try {
            PagoResponse pagoResponse = paymentService.verificarEstadoPago(pagoId);
            return ResponseEntity.ok(pagoResponse);
            
        } catch (RuntimeException e) {
            log.error("Error obteniendo estado del pago: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Obtener pagos de un pedido", 
               description = "Obtiene todos los pagos asociados a un pedido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagos obtenidos exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<PagoResponse>> obtenerPagosPorPedido(
            @Parameter(description = "ID del pedido") @PathVariable Long pedidoId) {
        
        List<PagoResponse> pagos = paymentService.obtenerPagosPorPedido(pedidoId);
        return ResponseEntity.ok(pagos);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener pagos exitosos de un usuario", 
               description = "Obtiene todos los pagos exitosos de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagos obtenidos exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<PagoResponse>> obtenerPagosExitososPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId,
            Authentication authentication) {
        
        List<PagoResponse> pagos = paymentService.obtenerPagosExitososPorUsuario(usuarioId);
        return ResponseEntity.ok(pagos);
    }
    
    @PostMapping("/verificar-pendientes")
    @Operation(summary = "Verificar pagos pendientes", 
               description = "Verifica y actualiza el estado de pagos pendientes (solo para administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación completada"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<String> verificarPagosPendientes(Authentication authentication) {
        
        // Verificar que el usuario sea administrador
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para realizar esta acción");
        }
        
        try {
            paymentService.verificarPagosPendientes();
            return ResponseEntity.ok("Verificación de pagos pendientes completada");
            
        } catch (Exception e) {
            log.error("Error verificando pagos pendientes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }
}
