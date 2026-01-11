package com.leandro.nexus_auth.dto;

import lombok.Data;

@Data
public class AsaasWebhookDTO {
    private String event;
    private Payment payment;

    @Data
    public static class Payment {
        private String id;
        private String customer;
        private String externalReference; // Aqui estará o USERNAME do seu usuário
        private String status;
    }
}