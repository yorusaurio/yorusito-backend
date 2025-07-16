package com.yorusito.backend.review.controller;

import com.yorusito.backend.review.dto.*;
import com.yorusito.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Gestión de reseñas de productos")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    @Operation(summary = "Crear reseña", description = "Crea una nueva reseña para un producto")
    public ResponseEntity<ReviewResponse> createReview(
            Authentication authentication,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse review = reviewService.createReview(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }
    
    @PutMapping("/{reviewId}")
    @Operation(summary = "Actualizar reseña", description = "Actualiza una reseña existente")
    public ResponseEntity<ReviewResponse> updateReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        ReviewResponse review = reviewService.updateReview(authentication.getName(), reviewId, request);
        return ResponseEntity.ok(review);
    }
    
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña existente")
    public ResponseEntity<Void> deleteReview(
            Authentication authentication,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(authentication.getName(), reviewId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/product/{productoId}")
    @Operation(summary = "Obtener reseñas de producto", description = "Obtiene todas las reseñas de un producto específico")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable Long productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewResponse> reviews = reviewService.getProductReviews(productoId, page, size);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user")
    @Operation(summary = "Obtener reseñas del usuario", description = "Obtiene todas las reseñas del usuario autenticado")
    public ResponseEntity<Page<ReviewResponse>> getUserReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewResponse> reviews = reviewService.getUserReviews(authentication.getName(), page, size);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/product/{productoId}/stats")
    @Operation(summary = "Obtener estadísticas de reseñas", description = "Obtiene estadísticas de reseñas de un producto")
    public ResponseEntity<ProductReviewStats> getProductReviewStats(@PathVariable Long productoId) {
        ProductReviewStats stats = reviewService.getProductReviewStats(productoId);
        return ResponseEntity.ok(stats);
    }
}
