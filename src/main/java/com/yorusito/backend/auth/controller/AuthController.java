package com.yorusito.backend.auth.controller;

import com.yorusito.backend.auth.dto.AuthResponse;
import com.yorusito.backend.auth.dto.LoginRequest;
import com.yorusito.backend.auth.dto.RegistroRequest;
import com.yorusito.backend.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro y login de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
