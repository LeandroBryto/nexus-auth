package com.leandro.nexus_auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnaliseRequestDTO {
    private String ip;
    private String url;
    private String metodo;
    private String userAgent;
    private String payload;
}