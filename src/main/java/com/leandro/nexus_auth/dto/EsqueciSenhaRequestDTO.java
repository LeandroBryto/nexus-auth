package com.leandro.nexus_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EsqueciSenhaRequestDTO {

    @NotBlank(message = "O e-mail é obrigatório para recuperação")
    @Email(message = "Informe um e-mail válido")
    private String email;
}