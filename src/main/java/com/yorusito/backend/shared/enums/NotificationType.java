package com.yorusito.backend.shared.enums;

public enum NotificationType {
    ORDER_CONFIRMED("Pedido confirmado"),
    ORDER_SHIPPED("Pedido enviado"),
    ORDER_DELIVERED("Pedido entregado"),
    ORDER_CANCELLED("Pedido cancelado"),
    PAYMENT_CONFIRMED("Pago confirmado"),
    PAYMENT_FAILED("Pago fallido"),
    STOCK_ALERT("Alerta de stock"),
    PROMOTIONAL("Promocional"),
    SYSTEM("Sistema");
    
    private final String descripcion;
    
    NotificationType(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
