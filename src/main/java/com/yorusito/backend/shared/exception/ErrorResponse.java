package com.yorusito.backend.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String mensaje;
    private String detalle;
    private int status;
    private LocalDateTime timestamp;
    private String path;
    private String error;
    private String message;
    private List<String> errores;
}
