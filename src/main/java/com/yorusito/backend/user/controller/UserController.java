package com.yorusito.backend.user.controller;

import com.yorusito.backend.user.dto.UpdateUserProfileRequest;
import com.yorusito.backend.user.dto.UserProfileResponse;
import com.yorusito.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Gestión de perfil de usuario")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    @Operation(summary = "Obtener perfil de usuario", description = "Obtiene el perfil completo del usuario autenticado")
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        UserProfileResponse profile = userService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/profile")
    @Operation(summary = "Actualizar perfil de usuario", description = "Actualiza el perfil del usuario autenticado")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        UserProfileResponse profile = userService.updateUserProfile(authentication.getName(), request);
        return ResponseEntity.ok(profile);
    }
}
