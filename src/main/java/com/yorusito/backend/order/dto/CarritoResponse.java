package com.yorusito.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponse {
    private List<CarritoItemResponse> items;
    private BigDecimal total;
    private Integer totalItems;
}
