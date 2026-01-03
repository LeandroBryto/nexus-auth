package com.leandro.nexus_auth.repository;

import com.leandro.nexus_auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUsername(String username);

    // --- ADICIONE ESTA LINHA ---
    Optional<Usuario> findByEmail(String email);
    // ---------------------------

    // Validações para evitar duplicidade no registro
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}