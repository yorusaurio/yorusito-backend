package com.yorusito.backend.review.service;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.auth.repository.UsuarioRepository;
import com.yorusito.backend.product.entity.Producto;
import com.yorusito.backend.product.repository.ProductoRepository;
import com.yorusito.backend.review.dto.*;
import com.yorusito.backend.review.entity.Review;
import com.yorusito.backend.review.repository.ReviewRepository;
import com.yorusito.backend.shared.exception.BadRequestException;
import com.yorusito.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public ReviewResponse createReview(String userEmail, CreateReviewRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        // Verificar si el usuario ya ha reseñado este producto
        if (reviewRepository.existsByProductoIdAndUsuarioIdAndActivoTrue(request.getProductoId(), usuario.getId())) {
            throw new BadRequestException("Ya has reseñado este producto");
        }
        
        Review review = Review.builder()
                .producto(producto)
                .usuario(usuario)
                .comment(request.getComment())
                .rating(request.getRating())
                .build();
        
        review = reviewRepository.save(review);
        
        return mapToResponse(review);
    }
    
    @Transactional
    public ReviewResponse updateReview(String userEmail, Long reviewId, UpdateReviewRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        Review review = reviewRepository.findByIdAndUsuarioIdAndActivoTrue(reviewId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Review no encontrada o no tienes permisos para editarla"));
        
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        
        review = reviewRepository.save(review);
        
        return mapToResponse(review);
    }
    
    @Transactional
    public void deleteReview(String userEmail, Long reviewId) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        Review review = reviewRepository.findByIdAndUsuarioIdAndActivoTrue(reviewId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Review no encontrada o no tienes permisos para eliminarla"));
        
        review.setActivo(false);
        reviewRepository.save(review);
    }
    
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(Long productoId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByProductoIdAndActivoTrueOrderByFechaCreacionDesc(productoId, pageable);
        
        return reviews.map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getUserReviews(String userEmail, int page, int size) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByUsuarioIdAndActivoTrueOrderByFechaCreacionDesc(usuario.getId(), pageable);
        
        return reviews.map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public ProductReviewStats getProductReviewStats(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        Double promedio = reviewRepository.findAverageRatingByProductoId(productoId);
        Long total = reviewRepository.countByProductoId(productoId);
        
        return ProductReviewStats.builder()
                .productoId(productoId)
                .nombreProducto(producto.getNombre())
                .promedioRating(promedio != null ? promedio : 0.0)
                .totalReviews(total)
                .rating5Stars(reviewRepository.countByProductoIdAndRating(productoId, 5))
                .rating4Stars(reviewRepository.countByProductoIdAndRating(productoId, 4))
                .rating3Stars(reviewRepository.countByProductoIdAndRating(productoId, 3))
                .rating2Stars(reviewRepository.countByProductoIdAndRating(productoId, 2))
                .rating1Star(reviewRepository.countByProductoIdAndRating(productoId, 1))
                .build();
    }
    
    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productoId(review.getProducto().getId())
                .nombreProducto(review.getProducto().getNombre())
                .nombreUsuario(review.getUsuario().getNombre())
                .comment(review.getComment())
                .rating(review.getRating())
                .fechaCreacion(review.getFechaCreacion())
                .fechaActualizacion(review.getFechaActualizacion())
                .build();
    }
}
