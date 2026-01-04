package com.leandro.nexus_auth.service;

import com.leandro.nexus_auth.dto.sentinel.SentinelErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

@Service
@Slf4j
public class SentinelService {

    @Value("${app.sentinel.url}")
    private String sentinelUrl;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.profiles.active:unknown}")
    private String activeProfile;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void reportError(String errorType, String message, String stackTrace, String user, String path) {
        try {
            if (sentinelUrl == null || sentinelUrl.isBlank()) {
                log.warn("URL do Sentinel não configurada. O erro não será reportado.");
                return;
            }

            SentinelErrorDTO errorDTO = SentinelErrorDTO.builder()
                    .serviceName(appName)
                    .errorType(errorType)
                    .message(message)
                    .stackTrace(stackTrace)
                    .user(user)
                    .path(path)
                    .timestamp(LocalDateTime.now())
                    .environment(activeProfile)
                    .build();

            // A rota da API do Sentinel para receber erros é /errors
            restTemplate.postForObject(sentinelUrl + "/errors", errorDTO, Void.class);
            
        } catch (Exception e) {
            log.error("Falha CRÍTICA ao reportar erro para o Sentinel: {}", e.getMessage());
        }
    }
}
