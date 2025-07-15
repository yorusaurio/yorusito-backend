package com.yorusito.backend.shared.enums;

public enum InventoryMovementType {
    ENTRADA("Entrada"),
    SALIDA("Salida"),
    AJUSTE("Ajuste"),
    DEVOLUCION("Devolución"),
    DAÑO("Daño"),
    TRANSFERENCIA("Transferencia");
    
    private final String descripcion;
    
    InventoryMovementType(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
