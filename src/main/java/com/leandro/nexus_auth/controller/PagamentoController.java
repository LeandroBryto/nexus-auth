package com.leandro.nexus_auth.controller;

import com.leandro.nexus_auth.model.Usuario;
import com.leandro.nexus_auth.repository.UsuarioRepository;
import com.leandro.nexus_auth.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/checkout-premium")
    public ResponseEntity<Map<String, String>> criarCheckout(Authentication authentication) {
        // Pega o username do usuário logado no sistema
        String username = authentication.getName();
        
        // Busca o usuário para pegar o email
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Gera o Pix e retorna os dados
        Map<String, String> dadosPix = pagamentoService.gerarPagamentoPix(username, usuario.getEmail());
        
        return ResponseEntity.ok(dadosPix);
    }
}
