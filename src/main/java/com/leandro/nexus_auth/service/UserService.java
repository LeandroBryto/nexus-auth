package com.leandro.nexus_auth.service;

import com.leandro.nexus_auth.enums.TipoPlano;
import com.leandro.nexus_auth.model.Usuario;
import com.leandro.nexus_auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void upgradeParaPremium(String username) {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));

        user.setPlano(TipoPlano.PREMIUM);
        usuarioRepository.save(user);
    }
}