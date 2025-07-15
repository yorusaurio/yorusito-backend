package com.yorusito.backend.shared.enums;

public enum OrderStatus {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    PROCESANDO("Procesando"),
    ENVIADO("Enviado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado"),
    DEVUELTO("Devuelto");
    
    private final String descripcion;
    
    OrderStatus(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
