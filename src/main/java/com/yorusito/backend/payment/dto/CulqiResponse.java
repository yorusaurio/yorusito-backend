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
public class CulqiResponse {
    
    private String id;
    private String object;
    private BigDecimal amount;
    private Boolean captured;
    private String currency_code;
    private String description;
    private String email;
    private String failure_message;
    private String installments;
    private String livemode;
    private Map<String, Object> metadata;
    private String outcome;
    private String reference_code;
    private String source_id;
    private String creation_date;
    private Boolean success;
    
    // Campos específicos para tokens
    private String card_number;
    private String expiration_month;
    private String expiration_year;
    private String card_brand;
    private String card_type;
    private String card_category;
    private String issuer;
    private String client;
    
    // Campos específicos para charges
    private Map<String, Object> source;
    private String dispute_reason;
    private String duplicate;
    private String fee;
    private String fee_details;
    private String net;
    private String paid;
    private String refunded;
    private String refunds;
    private String review;
    private String transfers;
}
