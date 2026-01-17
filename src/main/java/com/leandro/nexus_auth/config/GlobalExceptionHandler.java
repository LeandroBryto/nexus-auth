package com.leandro.nexus_auth.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private String getStackTraceAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest req) {
        // 1. Loga o erro localmente para debug imediato
        log.error("Exceção não tratada capturada: {}", ex.getMessage(), ex);

        // 2. Retorna uma resposta padrão para o cliente
        return ResponseEntity.status(500).body(Map.of("error", "Ocorreu um erro interno no servidor."));
    }
}
