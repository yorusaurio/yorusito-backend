package com.yorusito.backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CulqiChargeRequest {
    
    private BigDecimal amount;
    private String currency_code;
    private String email;
    private String source_id;
    private String description;
    private Map<String, Object> metadata;
}
