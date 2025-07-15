package com.yorusito.backend.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yorusito.backend.payment.config.CulqiConfig;
import com.yorusito.backend.payment.dto.CulqiChargeRequest;
import com.yorusito.backend.payment.dto.CulqiResponse;
import com.yorusito.backend.payment.dto.CulqiTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "culqi.enabled", havingValue = "true", matchIfMissing = false)
public class CulqiService {
    
    private final CulqiConfig culqiConfig;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;
    
    public CulqiService(CulqiConfig culqiConfig, ObjectMapper objectMapper) {
        this.culqiConfig = culqiConfig;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Crear un token de tarjeta en Culqi
     */
    public CulqiResponse createToken(CulqiTokenRequest tokenRequest) throws IOException {
        String url = culqiConfig.getBaseUrl() + "/tokens";
        
        String jsonBody = objectMapper.writeValueAsString(tokenRequest);
        
        RequestBody body = RequestBody.create(
            jsonBody,
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + culqiConfig.getPublicKey())
                .addHeader("Content-Type", "application/json")
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            log.info("Culqi token response: {}", responseBody);
            
            if (!response.isSuccessful()) {
                log.error("Error creando token en Culqi: {}", responseBody);
                throw new IOException("Error en Culqi: " + response.code() + " - " + responseBody);
            }
            
            return objectMapper.readValue(responseBody, CulqiResponse.class);
        }
    }
    
    /**
     * Crear un cargo en Culqi
     */
    public CulqiResponse createCharge(CulqiChargeRequest chargeRequest) throws IOException {
        String url = culqiConfig.getBaseUrl() + "/charges";
        
        String jsonBody = objectMapper.writeValueAsString(chargeRequest);
        
        RequestBody body = RequestBody.create(
            jsonBody,
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + culqiConfig.getSecretKey())
                .addHeader("Content-Type", "application/json")
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            log.info("Culqi charge response: {}", responseBody);
            
            if (!response.isSuccessful()) {
                log.error("Error creando cargo en Culqi: {}", responseBody);
                throw new IOException("Error en Culqi: " + response.code() + " - " + responseBody);
            }
            
            return objectMapper.readValue(responseBody, CulqiResponse.class);
        }
    }
    
    /**
     * Procesar pago completo: crear token y luego cargo
     */
    public CulqiResponse processPayment(String cardNumber, String cvv, String expMonth, 
                                       String expYear, String email, BigDecimal amount, 
                                       String description, Map<String, Object> metadata) throws IOException {
        
        // Paso 1: Crear token
        CulqiTokenRequest tokenRequest = CulqiTokenRequest.builder()
                .card_number(cardNumber)
                .cvv(cvv)
                .expiration_month(expMonth)
                .expiration_year(expYear)
                .email(email)
                .build();
        
        CulqiResponse tokenResponse = createToken(tokenRequest);
        
        if (tokenResponse.getId() == null) {
            throw new IOException("No se pudo crear el token en Culqi");
        }
        
        // Paso 2: Crear cargo usando el token
        CulqiChargeRequest chargeRequest = CulqiChargeRequest.builder()
                .amount(amount.multiply(BigDecimal.valueOf(100))) // Culqi usa centavos
                .currency_code("PEN")
                .email(email)
                .source_id(tokenResponse.getId())
                .description(description)
                .metadata(metadata)
                .build();
        
        return createCharge(chargeRequest);
    }
    
    /**
     * Consultar estado de un cargo
     */
    public CulqiResponse getCharge(String chargeId) throws IOException {
        String url = culqiConfig.getBaseUrl() + "/charges/" + chargeId;
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + culqiConfig.getSecretKey())
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            log.info("Culqi charge status response: {}", responseBody);
            
            if (!response.isSuccessful()) {
                log.error("Error consultando cargo en Culqi: {}", responseBody);
                throw new IOException("Error en Culqi: " + response.code() + " - " + responseBody);
            }
            
            return objectMapper.readValue(responseBody, CulqiResponse.class);
        }
    }
}
