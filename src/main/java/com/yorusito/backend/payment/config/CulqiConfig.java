package com.yorusito.backend.payment.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "culqi")
@ConditionalOnProperty(name = "culqi.enabled", havingValue = "true", matchIfMissing = false)
@Data
public class CulqiConfig {
    private String publicKey;
    private String secretKey;
    private String baseUrl = "https://api.culqi.com/v2";
    
    public boolean isConfigured() {
        return publicKey != null && !publicKey.isEmpty() && 
               secretKey != null && !secretKey.isEmpty() && 
               baseUrl != null && !baseUrl.isEmpty();
    }
}
