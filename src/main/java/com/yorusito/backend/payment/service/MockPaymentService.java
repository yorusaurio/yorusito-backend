package com.yorusito.backend.payment.service;

import com.yorusito.backend.order.entity.Pedido;
import com.yorusito.backend.order.repository.PedidoRepository;
import com.yorusito.backend.payment.dto.PagoRequest;
import com.yorusito.backend.payment.dto.PagoResponse;
import com.yorusito.backend.payment.entity.Pago;
import com.yorusito.backend.payment.repository.PagoRepository;
import com.yorusito.backend.shared.enums.EstadoPago;
import com.yorusito.backend.shared.enums.EstadoPedido;
import com.yorusito.backend.shared.enums.MetodoPago;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "culqi.enabled", havingValue = "false", matchIfMissing = true)
@Transactional
public class MockPaymentService implements PaymentServiceInterface {
    
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    
    /**
     * Procesar pago simulado (sin Culqi)
     */
    public PagoResponse procesarPago(PagoRequest pagoRequest) {
        try {
            // Verificar que el pedido existe
            Pedido pedido = pedidoRepository.findById(pagoRequest.getPedidoId())
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
            // Verificar que el pedido no esté ya pagado
            if (pagoRepository.existsByPedidoIdAndExitosoTrue(pagoRequest.getPedidoId())) {
                throw new RuntimeException("El pedido ya ha sido pagado");
            }
            
            // Simular procesamiento de pago
            log.info("MODO DEMO: Simulando pago con Culqi para pedido {}", pagoRequest.getPedidoId());
            
            // Crear registro de pago simulado
            Pago pago = Pago.builder()
                    .pedido(pedido)
                    .estado(EstadoPago.COMPLETADO)
                    .metodoPago(MetodoPago.TARJETA_CREDITO)
                    .monto(pagoRequest.getMonto())
                    .moneda(pagoRequest.getMoneda())
                    .emailComprador(pagoRequest.getEmailComprador())
                    .descripcion(pagoRequest.getDescripcion())
                    .fechaCreacion(LocalDateTime.now())
                    .fechaPago(LocalDateTime.now())
                    .culqiChargeId("demo_charge_" + UUID.randomUUID().toString().substring(0, 8))
                    .numeroTarjetaEnmascarada("****-****-****-" + pagoRequest.getNumeroTarjeta().substring(pagoRequest.getNumeroTarjeta().length() - 4))
                    .marcaTarjeta("VISA")
                    .exitoso(true)
                    .mensaje("Pago procesado exitosamente (MODO DEMO)")
                    .build();
            
            pago = pagoRepository.save(pago);
            
            // Actualizar estado del pedido
            pedido.setEstado(EstadoPedido.PAGADO);
            pedidoRepository.save(pedido);
            
            return convertirAPagoResponse(pago);
            
        } catch (Exception e) {
            log.error("Error procesando pago simulado: ", e);
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }
    
    /**
     * Verificar estado de un pago
     */
    public PagoResponse verificarEstadoPago(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        
        return convertirAPagoResponse(pago);
    }
    
    /**
     * Obtener pagos por pedido
     */
    @Transactional(readOnly = true)
    public List<PagoResponse> obtenerPagosPorPedido(Long pedidoId) {
        List<Pago> pagos = pagoRepository.findByPedidoId(pedidoId);
        return pagos.stream()
                .map(this::convertirAPagoResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener pagos exitosos de un usuario
     */
    @Transactional(readOnly = true)
    public List<PagoResponse> obtenerPagosExitososPorUsuario(Long usuarioId) {
        List<Pago> pagos = pagoRepository.findPagosExitososByUsuario(usuarioId);
        return pagos.stream()
                .map(this::convertirAPagoResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Procesar verificación de pagos pendientes (no hace nada en modo demo)
     */
    public void verificarPagosPendientes() {
        log.info("MODO DEMO: Verificación de pagos pendientes - No se requiere acción");
    }
    
    /**
     * Convertir entidad Pago a PagoResponse
     */
    private PagoResponse convertirAPagoResponse(Pago pago) {
        return PagoResponse.builder()
                .id(pago.getId())
                .pedidoId(pago.getPedido().getId())
                .culqiChargeId(pago.getCulqiChargeId())
                .estado(pago.getEstado())
                .metodoPago(pago.getMetodoPago())
                .monto(pago.getMonto())
                .moneda(pago.getMoneda())
                .emailComprador(pago.getEmailComprador())
                .descripcion(pago.getDescripcion())
                .fechaPago(pago.getFechaPago())
                .fechaCreacion(pago.getFechaCreacion())
                .mensaje(pago.getMensaje())
                .numeroTarjetaEnmascarada(pago.getNumeroTarjetaEnmascarada())
                .marcaTarjeta(pago.getMarcaTarjeta())
                .exitoso(pago.getExitoso())
                .build();
    }
}
