package com.yorusito.backend.payment.service;

import com.yorusito.backend.payment.dto.PagoRequest;
import com.yorusito.backend.payment.dto.PagoResponse;

import java.util.List;

public interface PaymentServiceInterface {
    
    PagoResponse procesarPago(PagoRequest pagoRequest);
    
    PagoResponse verificarEstadoPago(Long pagoId);
    
    List<PagoResponse> obtenerPagosPorPedido(Long pedidoId);
    
    List<PagoResponse> obtenerPagosExitososPorUsuario(Long usuarioId);
    
    void verificarPagosPendientes();
}
