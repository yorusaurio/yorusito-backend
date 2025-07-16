package com.yorusito.backend.whatsapp.controller;

import com.yorusito.backend.whatsapp.service.WhatsAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
@Tag(name = "WhatsApp", description = "Integración con WhatsApp para ventas")
public class WhatsAppController {
    
    private final WhatsAppService whatsAppService;
    
    @PostMapping("/generar-enlace")
    @Operation(summary = "Generar enlace personalizado", description = "Genera un enlace de WhatsApp con mensaje personalizado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Enlace generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Mensaje inválido")
    })
    public ResponseEntity<Map<String, String>> generarEnlacePersonalizado(
            @Parameter(description = "Mensaje a enviar por WhatsApp") @RequestBody Map<String, String> request) {
        
        String mensaje = request.get("mensaje");
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El mensaje no puede estar vacío"));
        }
        
        String enlace = whatsAppService.generarEnlaceWhatsAppSimple(mensaje);
        return ResponseEntity.ok(Map.of(
            "enlace", enlace,
            "mensaje", "Enlace generado exitosamente"
        ));
    }
    
    @GetMapping("/numero")
    @Operation(summary = "Obtener número de WhatsApp", description = "Obtiene el número de WhatsApp de la empresa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Número obtenido exitosamente")
    })
    public ResponseEntity<Map<String, String>> obtenerNumero() {
        return ResponseEntity.ok(Map.of(
            "numero", whatsAppService.getNumeroWhatsApp(),
            "formato", "Formato: 51999888777 (código país + número)"
        ));
    }
    
    @GetMapping("/info")
    @Operation(summary = "Información de contacto", description = "Obtiene información de contacto de la empresa")
    public ResponseEntity<Map<String, Object>> obtenerInfo() {
        return ResponseEntity.ok(Map.of(
            "numero", whatsAppService.getNumeroWhatsApp(),
            "empresa", "YORUSITO",
            "sitioWeb", "www.yorusito.com",
            "horarioAtencion", "Lunes a Viernes: 9:00 AM - 6:00 PM",
            "mensaje", "¡Contáctanos por WhatsApp para resolver tus dudas!"
        ));
    }
}
