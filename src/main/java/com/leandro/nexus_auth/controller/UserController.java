package com.leandro.nexus_auth.controller;

import com.leandro.nexus_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Esse método agora é só para o ADMIN ou uso interno via Webhook
    @PatchMapping("/{username}/upgrade")
    @PreAuthorize("hasRole('ADMIN')") // Proteja, para nenhum espertinho se dar upgrade grátis
    public ResponseEntity<String> upgradeManual(@PathVariable String username) {
        userService.upgradeParaPremium(username);
        return ResponseEntity.ok("Upgrade realizado manualmente para: " + username);
    }
}
