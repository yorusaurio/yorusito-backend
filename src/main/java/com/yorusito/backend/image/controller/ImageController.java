package com.yorusito.backend.image.controller;

import com.yorusito.backend.image.service.ImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Imágenes", description = "Subida y gestión de imágenes usando Img.bb")
@SecurityRequirement(name = "bearerAuth")
public class ImageController {
    
    private final ImageUploadService imageUploadService;
    
    @PostMapping("/upload")
    @Operation(summary = "Subir imagen", description = "Sube una imagen a Img.bb y retorna la URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen subida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Archivo inválido"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @Parameter(description = "Archivo de imagen a subir") 
            @RequestParam("image") MultipartFile file) {
        
        try {
            String imageUrl = imageUploadService.uploadImage(file);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Imagen subida exitosamente",
                "imageUrl", imageUrl,
                "fileName", file.getOriginalFilename(),
                "fileSize", file.getSize()
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación subiendo imagen: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
            
        } catch (Exception e) {
            log.error("Error inesperado subiendo imagen: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error interno del servidor"
            ));
        }
    }
    
    @PostMapping("/upload-base64")
    @Operation(summary = "Subir imagen desde Base64", description = "Sube una imagen desde cadena Base64 a Img.bb")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen subida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Base64 inválido"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadImageFromBase64(
            @RequestBody Map<String, String> request) {
        
        try {
            String base64Image = request.get("image");
            String fileName = request.get("fileName");
            
            String imageUrl = imageUploadService.uploadImageFromBase64(base64Image, fileName);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Imagen subida exitosamente",
                "imageUrl", imageUrl,
                "fileName", fileName != null ? fileName : "imagen_base64"
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación subiendo imagen Base64: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
            
        } catch (Exception e) {
            log.error("Error inesperado subiendo imagen Base64: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error interno del servidor"
            ));
        }
    }
    
    @GetMapping("/info")
    @Operation(summary = "Información del servicio", description = "Obtiene información sobre el servicio de imágenes")
    public ResponseEntity<Map<String, Object>> getServiceInfo() {
        return ResponseEntity.ok(Map.of(
            "service", "Img.bb",
            "maxFileSize", "32MB",
            "supportedFormats", new String[]{"JPG", "JPEG", "PNG", "GIF", "BMP", "WEBP"},
            "features", new String[]{"Sin expiración", "URLs directas", "Alta disponibilidad"}
        ));
    }
}
