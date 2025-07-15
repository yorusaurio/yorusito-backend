package com.yorusito.backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CulqiTokenRequest {
    
    private String card_number;
    private String cvv;
    private String expiration_month;
    private String expiration_year;
    private String email;
}
