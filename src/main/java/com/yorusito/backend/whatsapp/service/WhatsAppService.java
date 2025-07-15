package com.yorusito.backend.whatsapp.service;

import com.yorusito.backend.order.entity.CarritoItem;
import com.yorusito.backend.auth.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {
    
    private static final String WHATSAPP_BASE_URL = "https://wa.me/";
    private static final DecimalFormat FORMATO_PRECIO = new DecimalFormat("#.00");
    
    @Value("${whatsapp.numero:51999888777}")
    private String numeroWhatsApp;
    
    @Value("${whatsapp.mensaje.empresa:YORUSITO}")
    private String nombreEmpresa;
    
    @Value("${whatsapp.mensaje.web:www.yorusito.com}")
    private String sitioWeb;
    
    public String generarEnlaceWhatsApp(Usuario usuario, List<CarritoItem> items) {
        try {
            String mensaje = construirMensaje(usuario, items);
            String mensajeCodificado = URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
            
            String enlace = WHATSAPP_BASE_URL + numeroWhatsApp + "?text=" + mensajeCodificado;
            
            log.info("Enlace WhatsApp generado para usuario: {}", usuario.getEmail());
            return enlace;
            
        } catch (Exception e) {
            log.error("Error generando enlace WhatsApp: ", e);
            throw new RuntimeException("Error generando enlace de WhatsApp", e);
        }
    }
    
    private String construirMensaje(Usuario usuario, List<CarritoItem> items) {
        StringBuilder mensaje = new StringBuilder();
        
        // Saludo y datos del cliente
        mensaje.append("🛒 *NUEVO PEDIDO - ").append(nombreEmpresa).append("*\n");
        mensaje.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        mensaje.append("👤 *DATOS DEL CLIENTE:*\n");
        mensaje.append("• Nombre: ").append(usuario.getNombre()).append("\n");
        mensaje.append("• Email: ").append(usuario.getEmail()).append("\n");
        if (usuario.getTelefono() != null && !usuario.getTelefono().isEmpty()) {
            mensaje.append("• Teléfono: ").append(usuario.getTelefono()).append("\n");
        }
        mensaje.append("• Fecha: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        
        // Detalle de productos
        mensaje.append("📦 *DETALLE DEL PEDIDO:*\n");
        mensaje.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        BigDecimal total = BigDecimal.ZERO;
        int numeroItem = 1;
        
        for (CarritoItem item : items) {
            BigDecimal subtotal = item.getProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
            
            mensaje.append(numeroItem).append(". *").append(item.getProducto().getNombre()).append("*\n");
            mensaje.append("   • Cantidad: ").append(item.getCantidad()).append("\n");
            mensaje.append("   • Precio unit: S/ ").append(FORMATO_PRECIO.format(item.getProducto().getPrecio())).append("\n");
            mensaje.append("   • Subtotal: S/ ").append(FORMATO_PRECIO.format(subtotal)).append("\n");
            
            if (item.getProducto().getDescripcion() != null && !item.getProducto().getDescripcion().isEmpty()) {
                String descripcionCorta = item.getProducto().getDescripcion().length() > 50 
                    ? item.getProducto().getDescripcion().substring(0, 50) + "..."
                    : item.getProducto().getDescripcion();
                mensaje.append("   • Descripción: ").append(descripcionCorta).append("\n");
            }
            
            mensaje.append("\n");
            numeroItem++;
        }
        
        // Total
        mensaje.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        mensaje.append("💰 *TOTAL: S/ ").append(FORMATO_PRECIO.format(total)).append("*\n");
        mensaje.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        // Información adicional
        mensaje.append("📝 *INFORMACIÓN ADICIONAL:*\n");
        mensaje.append("• Método de pago: Por coordinar\n");
        mensaje.append("• Envío: Por coordinar\n");
        mensaje.append("• Estado: Pendiente de confirmación\n\n");
        
        mensaje.append("¡Gracias por tu compra! 🎉\n");
        mensaje.append("Te contactaremos pronto para confirmar tu pedido y coordinar el envío.\n\n");
        mensaje.append("_Mensaje generado automáticamente desde ").append(sitioWeb).append("_");
        
        return mensaje.toString();
    }
    
    public String generarEnlaceWhatsAppSimple(String mensaje) {
        try {
            String mensajeCodificado = URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
            return WHATSAPP_BASE_URL + numeroWhatsApp + "?text=" + mensajeCodificado;
        } catch (Exception e) {
            log.error("Error generando enlace WhatsApp simple: ", e);
            throw new RuntimeException("Error generando enlace de WhatsApp", e);
        }
    }
    
    public String getNumeroWhatsApp() {
        return numeroWhatsApp;
    }
}
