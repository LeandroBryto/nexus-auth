package com.leandro.nexus_auth.controller;

import com.leandro.nexus_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <--- IMPORTANTE
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- AQUI ESTÁ A PROTEÇÃO ---
    // Isso diz: "Se o token não tiver ROLE_ADMIN, rejeite com erro 403"
    @PatchMapping("/{username}/upgrade")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> upgradeParaPremium(@PathVariable String username) {

        userService.upgradeParaPremium(username);

        return ResponseEntity.ok("Upgrade realizado com sucesso para o usuário: " + username);
    }
}