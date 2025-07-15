package com.yorusito.backend.user.repository;

import com.yorusito.backend.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    Optional<UserProfile> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT up FROM UserProfile up WHERE up.usuario.email = :email")
    Optional<UserProfile> findByUsuarioEmail(@Param("email") String email);
    
    boolean existsByUsuarioId(Long usuarioId);
}
