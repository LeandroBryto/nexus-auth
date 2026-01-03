package com.leandro.nexus_auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnaliseResponseDTO {
    private boolean permitido;
    private String mensagem;
    private String tipoAmeaca;
}