package com.yorusito.backend.user.service;

import com.yorusito.backend.auth.entity.Usuario;
import com.yorusito.backend.auth.repository.UsuarioRepository;
import com.yorusito.backend.user.dto.UpdateUserProfileRequest;
import com.yorusito.backend.user.dto.UserProfileResponse;
import com.yorusito.backend.user.entity.UserProfile;
import com.yorusito.backend.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserProfileRepository userProfileRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String email) {
        UserProfile profile = userProfileRepository.findByUsuarioEmail(email)
                .orElseGet(() -> createDefaultProfile(email));
        
        return UserProfileResponse.builder()
                .id(profile.getId())
                .nombre(profile.getUsuario().getNombre())
                .email(profile.getUsuario().getEmail())
                .telefono(profile.getUsuario().getTelefono())
                .direccionEnvio(profile.getDireccionEnvio())
                .ciudad(profile.getCiudad())
                .codigoPostal(profile.getCodigoPostal())
                .pais(profile.getPais())
                .telefonoAdicional(profile.getTelefonoAdicional())
                .fechaNacimiento(profile.getFechaNacimiento())
                .preferenciasNotificaciones(profile.getPreferenciasNotificaciones())
                .aceptaMarketing(profile.getAceptaMarketing())
                .fechaActualizacion(profile.getFechaActualizacion())
                .build();
    }
    
    @Transactional
    public UserProfileResponse updateUserProfile(String email, UpdateUserProfileRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        // Actualizar datos del usuario
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuarioRepository.save(usuario);
        
        // Actualizar o crear perfil
        UserProfile profile = userProfileRepository.findByUsuarioId(usuario.getId())
                .orElse(UserProfile.builder()
                        .usuario(usuario)
                        .build());
        
        profile.setDireccionEnvio(request.getDireccionEnvio());
        profile.setCiudad(request.getCiudad());
        profile.setCodigoPostal(request.getCodigoPostal());
        profile.setPais(request.getPais());
        profile.setTelefonoAdicional(request.getTelefonoAdicional());
        profile.setFechaNacimiento(request.getFechaNacimiento());
        profile.setPreferenciasNotificaciones(request.getPreferenciasNotificaciones());
        profile.setAceptaMarketing(request.getAceptaMarketing());
        
        userProfileRepository.save(profile);
        
        return getUserProfile(request.getEmail());
    }
    
    private UserProfile createDefaultProfile(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        UserProfile profile = UserProfile.builder()
                .usuario(usuario)
                .build();
        
        return userProfileRepository.save(profile);
    }
}
