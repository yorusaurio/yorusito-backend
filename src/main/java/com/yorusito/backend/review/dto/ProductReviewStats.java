package com.yorusito.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewStats {
    
    private Long productoId;
    private String nombreProducto;
    private Double promedioRating;
    private Long totalReviews;
    private Long rating5Stars;
    private Long rating4Stars;
    private Long rating3Stars;
    private Long rating2Stars;
    private Long rating1Star;
}
