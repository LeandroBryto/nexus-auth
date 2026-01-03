package com.leandro.nexus_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

    private String para;       // Destinatário (ex: leandro@email.com)
    private String assunto;    // Título do E-mail
    private String corpo;      // Mensagem (Texto ou HTML)

    // Opcional: Variáveis para templates HTML (se for usar Thymeleaf depois)
    private Map<String, Object> variaveis;
}