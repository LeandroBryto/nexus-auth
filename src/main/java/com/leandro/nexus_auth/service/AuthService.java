package com.leandro.nexus_auth.service;

import com.leandro.nexus_auth.dto.*;
import com.leandro.nexus_auth.enums.PerfilUsuario;
import com.leandro.nexus_auth.enums.TipoPlano;
import com.leandro.nexus_auth.model.Usuario;
import com.leandro.nexus_auth.repository.UsuarioRepository;
import com.leandro.nexus_auth.security.JwtService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;
    private final UserService userService;

    // 1. REGISTRO (Usa RegistroDTO)
    @Transactional
    public AuthenticationResponseDTO register(RegistroDTO request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username já existe.");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já existe.");
        }

        Usuario user = Usuario.builder()
                .nomeCompleto(request.getNomeCompleto())
                .username(request.getUsername())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .perfil(PerfilUsuario.USUARIO)
                .plano(TipoPlano.FREE)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .build();

        usuarioRepository.save(user);

        // Envia email simples
        enviarEmail(user.getEmail(), "Bem-vindo", "<p>Conta criada com sucesso!</p>");

        // --- AQUI ESTÁ A MUDANÇA ---
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user); // <--- TEM QUE TER ESSA LINHA

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                // .tokenType("Bearer")
                .build();
    }

    // 2. LOGIN (Usa LoginRequestDTO)
    public AuthenticationResponseDTO login(LoginRequestDTO request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getSenha())
        );

        Usuario user = (Usuario) authentication.getPrincipal();
        String clientIp = getClientIp(httpRequest);
        userService.registrarLoginAsync(user, clientIp);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    // 3. ESQUECI SENHA (Usa EsqueciSenhaRequestDTO)
    @Transactional
    public void esqueciSenha(EsqueciSenhaRequestDTO request) {
        Usuario user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado."));

        // Gera senha temporária
        String senhaTemporaria = gerarSenhaAleatoria(8);

        // Salva encriptada no banco
        user.setSenha(passwordEncoder.encode(senhaTemporaria));
        usuarioRepository.save(user);

        // Manda limpa pro email
        String html = "<p>Sua senha temporária é: <b>" + senhaTemporaria + "</b></p>";
        enviarEmail(user.getEmail(), "Recuperação de Senha", html);
    }

    // 4. REDEFINIR SENHA (Usa RedefinirSenhaRequestDTO)
    @Transactional
    public void redefinirSenha(RedefinirSenhaRequestDTO request) {
        // Busca pelo email
        Usuario user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Verifica se a senha TEMPORÁRIA (que foi no email) bate com a do banco
        if (!passwordEncoder.matches(request.getSenhaTemporaria(), user.getPassword())) {
            throw new RuntimeException("A senha temporária está incorreta.");
        }

        // Define a NOVA senha
        user.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(user);
    }

    // --- MÉTODOS AUXILIARES ---

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String ip = request.getHeader("X-FORWARDED-FOR");
        return (ip == null || "".equals(ip)) ? request.getRemoteAddr() : ip;
    }

    private String gerarSenhaAleatoria(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@$#";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void enviarEmail(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Erro ao enviar email", e);
        }
    }
}
