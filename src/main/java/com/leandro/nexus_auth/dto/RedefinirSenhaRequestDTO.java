package com.leandro.nexus_auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // <--- Essa anotação é obrigatória para o .get...() funcionar
public class RedefinirSenhaRequestDTO {

    @NotBlank(message = "O e-mail é obrigatório")
    private String email; // Gera o getEmail()

    @NotBlank(message = "A senha temporária é obrigatória")
    private String senhaTemporaria; // Gera o getSenhaTemporaria()

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String novaSenha; // Gera o getNovaSenha()
}