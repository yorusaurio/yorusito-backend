package com.yorusito.backend.payment.service;

import com.yorusito.backend.order.entity.Pedido;
import com.yorusito.backend.order.repository.PedidoRepository;
import com.yorusito.backend.payment.dto.CulqiResponse;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "culqi.enabled", havingValue = "true", matchIfMissing = false)
@Transactional
public class PaymentService implements PaymentServiceInterface {
    
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final CulqiService culqiService;
    
    /**
     * Procesar pago con tarjeta usando Culqi
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
            
            // Crear registro de pago inicial
            Pago pago = Pago.builder()
                    .pedido(pedido)
                    .estado(EstadoPago.PROCESANDO)
                    .metodoPago(MetodoPago.TARJETA_CREDITO)
                    .monto(pagoRequest.getMonto())
                    .moneda(pagoRequest.getMoneda())
                    .emailComprador(pagoRequest.getEmailComprador())
                    .descripcion(pagoRequest.getDescripcion())
                    .fechaCreacion(LocalDateTime.now())
                    .exitoso(false)
                    .build();
            
            pago = pagoRepository.save(pago);
            
            // Procesar pago con Culqi
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("pedido_id", pedido.getId());
            metadata.put("usuario_id", pedido.getUsuario().getId());
            metadata.put("pago_id", pago.getId());
            
            CulqiResponse culqiResponse = culqiService.processPayment(
                    pagoRequest.getNumeroTarjeta(),
                    pagoRequest.getCvv(),
                    pagoRequest.getMesExpiracion(),
                    pagoRequest.getAnioExpiracion(),
                    pagoRequest.getEmailComprador(),
                    pagoRequest.getMonto(),
                    pagoRequest.getDescripcion(),
                    metadata
            );
            
            // Actualizar registro de pago con respuesta de Culqi
            actualizarPagoConRespuestaCulqi(pago, culqiResponse);
            
            // Si el pago fue exitoso, actualizar el pedido
            if (pago.getExitoso()) {
                pedido.setEstado(EstadoPedido.PAGADO);
                pedidoRepository.save(pedido);
            }
            
            return convertirAPagoResponse(pago);
            
        } catch (IOException e) {
            log.error("Error procesando pago: ", e);
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado procesando pago: ", e);
            throw new RuntimeException("Error interno procesando el pago");
        }
    }
    
    /**
     * Verificar estado de un pago
     */
    public PagoResponse verificarEstadoPago(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        
        // Si el pago está pendiente y tiene un charge ID, verificar con Culqi
        if (pago.getEstado() == EstadoPago.PROCESANDO && pago.getCulqiChargeId() != null) {
            try {
                CulqiResponse culqiResponse = culqiService.getCharge(pago.getCulqiChargeId());
                actualizarPagoConRespuestaCulqi(pago, culqiResponse);
                
                // Si el pago cambió a exitoso, actualizar el pedido
                if (pago.getExitoso() && pago.getPedido().getEstado() != EstadoPedido.PAGADO) {
                    pago.getPedido().setEstado(EstadoPedido.PAGADO);
                    pedidoRepository.save(pago.getPedido());
                }
                
            } catch (IOException e) {
                log.error("Error verificando estado del pago con Culqi: ", e);
            }
        }
        
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
     * Procesar verificación de pagos pendientes (para tarea programada)
     */
    public void verificarPagosPendientes() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusMinutes(10);
        List<Pago> pagosPendientes = pagoRepository.findPagosPendientesParaVerificar(EstadoPago.PROCESANDO, fechaLimite);
        
        for (Pago pago : pagosPendientes) {
            try {
                if (pago.getCulqiChargeId() != null) {
                    CulqiResponse culqiResponse = culqiService.getCharge(pago.getCulqiChargeId());
                    actualizarPagoConRespuestaCulqi(pago, culqiResponse);
                    
                    if (pago.getExitoso()) {
                        pago.getPedido().setEstado(EstadoPedido.PAGADO);
                        pedidoRepository.save(pago.getPedido());
                    }
                }
            } catch (IOException e) {
                log.error("Error verificando pago pendiente ID {}: ", pago.getId(), e);
            }
        }
    }
    
    /**
     * Actualizar pago con respuesta de Culqi
     */
    private void actualizarPagoConRespuestaCulqi(Pago pago, CulqiResponse culqiResponse) {
        pago.setCulqiChargeId(culqiResponse.getId());
        pago.setFechaPago(LocalDateTime.now());
        
        if (culqiResponse.getSuccess() != null && culqiResponse.getSuccess()) {
            pago.setEstado(EstadoPago.COMPLETADO);
            pago.setExitoso(true);
            pago.setMensaje("Pago procesado exitosamente");
        } else {
            pago.setEstado(EstadoPago.FALLIDO);
            pago.setExitoso(false);
            pago.setMensaje(culqiResponse.getFailure_message() != null ? 
                    culqiResponse.getFailure_message() : "Pago rechazado");
        }
        
        // Información adicional de la tarjeta
        if (culqiResponse.getSource() != null) {
            Map<String, Object> source = culqiResponse.getSource();
            pago.setNumeroTarjetaEnmascarada((String) source.get("card_number"));
            pago.setMarcaTarjeta((String) source.get("card_brand"));
        }
        
        pagoRepository.save(pago);
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
