package com.leandro.nexus_auth.config.sentinel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leandro.nexus_auth.dto.AnaliseRequestDTO;
import com.leandro.nexus_auth.dto.AnaliseResponseDTO;
import com.leandro.nexus_auth.utils.SentinelClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SentinelProtectionFilter extends OncePerRequestFilter {

    private final SentinelClient sentinelClient;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Ignora OPTIONS e Swagger
        String path = request.getRequestURI();
        if (request.getMethod().equals("OPTIONS") || path.contains("swagger") || path.contains("api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Envelopar para ler o corpo (Vírus/Payload)
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        try {
            String bodyReal = wrappedRequest.getBodyAsString();
            if (bodyReal == null) bodyReal = "";

            // 2. Montar DTO para o Sentinela
            AnaliseRequestDTO analiseRequest = AnaliseRequestDTO.builder()
                    .ip(getClientIp(request)) // Método auxiliar para pegar IP real
                    .url(request.getRequestURI())
                    .metodo(request.getMethod())
                    .userAgent(request.getHeader("User-Agent"))
                    .payload(bodyReal)
                    .build();

            // 3. O Sentinela Julga
            AnaliseResponseDTO veredito = sentinelClient.analisarTrafego(analiseRequest);

            // 4. Se o Sentinela bloquear
            if (!veredito.isPermitido()) {
                log.warn("BLOQUEIO SENTINELA: IP={} Motivo={}", analiseRequest.getIp(), veredito.getMensagem());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(veredito));
                return; // Bloqueia a requisição aqui
            }

        } catch (Exception e) {
            log.error("Erro ao conectar com Sentinela (Fail-open): {}", e.getMessage());
            // Se o Sentinela cair, a gente deixa passar para não travar o login (Fail-Open)
            // Ou bloqueia (Fail-Close) dependendo da sua política.
        }

        // 5. Passa a requisição ENVELOPADA
        filterChain.doFilter(wrappedRequest, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        return (ip == null || "".equals(ip)) ? request.getRemoteAddr() : ip;
    }
}