package com.leandro.nexus_auth.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PagamentoService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public Map<String, String> gerarPagamentoPix(String username, String email) {
        PaymentClient client = new PaymentClient();

        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(new BigDecimal("5.00")) // Valor do plano: R$ 5,00
                .description("Plano Premium Nexus Auth")
                .paymentMethodId("pix")
                .externalReference(username) // Identificador do usuário para o Webhook
                // .notificationUrl("https://seu-dominio.com/api/v1/public/notifications/mercadopago") // Descomente e ajuste se tiver domínio fixo
                .payer(PaymentPayerRequest.builder()
                        .email(email)
                        .build())
                .build();

        try {
            Payment payment = client.create(request);
            
            Map<String, String> response = new HashMap<>();
            // Código "Copia e Cola"
            if (payment.getPointOfInteraction() != null && payment.getPointOfInteraction().getTransactionData() != null) {
                response.put("pix_code", payment.getPointOfInteraction().getTransactionData().getQrCode());
                response.put("qr_code_base64", payment.getPointOfInteraction().getTransactionData().getQrCodeBase64());
            } else {
                throw new RuntimeException("O Mercado Pago não retornou os dados do PIX. Status: " + payment.getStatus());
            }
            
            return response;
        } catch (MPApiException e) {
            // Erro vindo da API do Mercado Pago (ex: dados inválidos ou bloqueio de política)
            String errorContent = e.getApiResponse().getContent();
            System.err.println("Erro detalhado do MP: " + errorContent);
            
            if (errorContent.contains("PA_UNAUTHORIZED_RESULT_FROM_POLICIES")) {
                 throw new RuntimeException("Mercado Pago bloqueou a requisição. Verifique se as credenciais de produção estão ativas no painel e se você não está comprando de si mesmo.");
            }
            
            throw new RuntimeException("Erro API Mercado Pago: " + errorContent);
        } catch (MPException e) {
            // Erro interno do SDK ou de conexão
            throw new RuntimeException("Erro SDK Mercado Pago: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erro desconhecido ao gerar Pix: " + e.getMessage());
        }
    }
}
