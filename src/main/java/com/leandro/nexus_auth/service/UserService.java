package com.leandro.nexus_auth.service;

import com.leandro.nexus_auth.enums.TipoPlano;
import com.leandro.nexus_auth.model.Usuario;
import com.leandro.nexus_auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void upgradeParaPremium(String username) {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));

        user.setPlano(TipoPlano.PREMIUM);
        usuarioRepository.save(user);
    }

    @Async
    @Transactional
    public void registrarLoginAsync(Usuario user, String ip) {
        try {
            user.setDataUltimoLogin(LocalDateTime.now());
            user.setIpUltimoLogin(ip);
            usuarioRepository.save(user);
        } catch (Exception e) {
            log.error("Erro ao registrar último login para {}: {}", user.getUsername(), e.getMessage(), e);
        }
    }
}
