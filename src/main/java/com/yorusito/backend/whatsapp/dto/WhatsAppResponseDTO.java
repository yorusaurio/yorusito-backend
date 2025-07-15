package com.yorusito.backend.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppResponseDTO {
    private String enlaceWhatsApp;
    private String mensaje;
    private BigDecimal total;
    private int cantidadItems;
    private LocalDateTime fechaGeneracion;
    private String numeroWhatsApp;
    private boolean exitoso;
}
