package com.yorusito.backend.image.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class ImageUploadService {
    
    @Value("${imgbb.api.key:8d8f4daf7098ef51df00c7e9401de314}")
    private String imgbbApiKey;
    
    private static final String IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload";
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ImageUploadService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Subir imagen a Img.bb desde MultipartFile
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        
        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }
        
        // Validar tamaño (máximo 32MB según Img.bb)
        if (file.getSize() > 32 * 1024 * 1024) {
            throw new IllegalArgumentException("El archivo es demasiado grande (máximo 32MB)");
        }
        
        // Convertir a Base64
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        
        return uploadToImgbb(base64Image, file.getOriginalFilename());
    }
    
    /**
     * Subir imagen a Img.bb desde Base64
     */
    public String uploadImageFromBase64(String base64Image, String fileName) throws IOException {
        if (base64Image == null || base64Image.trim().isEmpty()) {
            throw new IllegalArgumentException("La imagen en Base64 no puede estar vacía");
        }
        
        // Limpiar el prefijo data:image si existe
        if (base64Image.startsWith("data:image/")) {
            base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
        }
        
        return uploadToImgbb(base64Image, fileName);
    }
    
    /**
     * Método privado para subir a Img.bb
     */
    private String uploadToImgbb(String base64Image, String fileName) throws IOException {
        try {
            // Preparar el cuerpo de la petición
            RequestBody formBody = new FormBody.Builder()
                    .add("key", imgbbApiKey)
                    .add("image", base64Image)
                    .add("name", fileName != null ? fileName : "producto_" + System.currentTimeMillis())
                    .add("expiration", "0") // 0 = nunca expira
                    .build();
            
            // Crear la petición
            Request request = new Request.Builder()
                    .url(IMGBB_UPLOAD_URL)
                    .post(formBody)
                    .build();
            
            // Ejecutar la petición
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();
                
                if (!response.isSuccessful()) {
                    log.error("Error subiendo imagen a Img.bb: {}", responseBody);
                    throw new IOException("Error al subir imagen: " + response.code());
                }
                
                // Parsear la respuesta JSON
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                if (!jsonResponse.get("success").asBoolean()) {
                    log.error("Img.bb reportó error: {}", responseBody);
                    throw new IOException("Error en el servicio de imágenes");
                }
                
                // Extraer la URL de la imagen
                String imageUrl = jsonResponse.get("data").get("url").asText();
                log.info("Imagen subida exitosamente: {}", imageUrl);
                
                return imageUrl;
                
            }
        } catch (Exception e) {
            log.error("Error inesperado subiendo imagen: ", e);
            throw new IOException("Error al procesar la imagen: " + e.getMessage());
        }
    }
    
    /**
     * Validar si una URL es de Img.bb
     */
    public boolean isImgbbUrl(String url) {
        return url != null && (url.contains("ibb.co") || url.contains("imgbb.com"));
    }
    
    /**
     * Obtener información de una imagen subida
     */
    public String getImageInfo(String imageUrl) {
        if (!isImgbbUrl(imageUrl)) {
            return "URL no es de Img.bb";
        }
        
        return "Imagen alojada en Img.bb: " + imageUrl;
    }
}
