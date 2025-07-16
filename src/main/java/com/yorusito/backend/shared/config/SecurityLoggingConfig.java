package com.yorusito.backend.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@Slf4j
public class SecurityLoggingConfig {

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, 
                org.springframework.security.core.AuthenticationException authException) -> {
            
            String requestPath = request.getRequestURI();
            String method = request.getMethod();
            String queryString = request.getQueryString();
            String fullPath = queryString != null ? requestPath + "?" + queryString : requestPath;
            
            log.error("🚫 AUTHENTICATION FAILED:");
            log.error("   📍 Path: {} {}", method, fullPath);
            log.error("   🔍 Headers: Authorization={}", request.getHeader("Authorization") != null ? "Present" : "Absent");
            log.error("   ❌ Reason: {}", authException.getMessage());
            log.error("   💡 Context Path: {}", request.getContextPath());
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\",\"method\":\"%s\"}", 
                authException.getMessage(), fullPath, method));
        };
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, 
                org.springframework.security.access.AccessDeniedException accessDeniedException) -> {
            
            String requestPath = request.getRequestURI();
            String method = request.getMethod();
            String queryString = request.getQueryString();
            String fullPath = queryString != null ? requestPath + "?" + queryString : requestPath;
            
            log.error("🔒 ACCESS DENIED:");
            log.error("   📍 Path: {} {}", method, fullPath);
            log.error("   👤 User: {}", request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous");
            log.error("   ❌ Reason: {}", accessDeniedException.getMessage());
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"error\":\"Access Denied\",\"message\":\"%s\",\"path\":\"%s\",\"method\":\"%s\"}", 
                accessDeniedException.getMessage(), fullPath, method));
        };
    }
}
