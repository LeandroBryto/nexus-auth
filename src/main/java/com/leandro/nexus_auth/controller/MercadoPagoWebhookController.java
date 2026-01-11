package com.leandro.nexus_auth.controller;

import com.leandro.nexus_auth.service.UserService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/notifications")
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoWebhookController {

    private final UserService userService;
    
    @Value("${mercadopago.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> receberNotificacao(
            @RequestHeader(value = "x-signature", required = false) String signature,
            @RequestBody Map<String, Object> payload) {

        // 1. Verificação básica de segurança (Opcional, mas recomendado)
        // Se quiser validar a assinatura HMAC, o código vai aqui.
        
        log.info("Notificação recebida do Mercado Pago: {}", payload);

        // 2. O Mercado Pago avisa que um pagamento foi criado ou atualizado
        String action = String.valueOf(payload.get("action"));
        
        if ("payment.created".equals(action) || "payment.updated".equals(action)) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            
            if (data != null && data.containsKey("id")) {
                String paymentId = String.valueOf(data.get("id"));
                processarPagamento(paymentId);
            }
        }

        return ResponseEntity.ok().build();
    }

    private void processarPagamento(String paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));

            // Só libera se o status for "approved"
            if ("approved".equals(payment.getStatus())) {
                // Recuperamos o username que enviamos no checkout
                String username = payment.getExternalReference();
                
                log.info("Pagamento aprovado! Fazendo upgrade do usuário: {}", username);
                userService.upgradeParaPremium(username);
            }
        } catch (Exception e) {
            log.error("Erro ao consultar pagamento no Mercado Pago: {}", e.getMessage());
        }
    }
}
