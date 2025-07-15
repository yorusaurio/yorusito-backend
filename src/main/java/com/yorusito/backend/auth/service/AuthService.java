package com.yorusito.backend.auth.service;

import com.yorusito.backend.auth.dto.AuthResponse;
import com.yorusito.backend.auth.dto.LoginRequest;
import com.yorusito.backend.auth.dto.RegistroRequest;
import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.auth.repository.UsuarioRepository;
import com.yorusito.backend.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .build();

        usuarioRepository.save(usuario);

        String jwtToken = jwtTokenProvider.generateToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String jwtToken = jwtTokenProvider.generateToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }
}
