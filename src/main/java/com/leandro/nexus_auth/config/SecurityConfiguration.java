package com.leandro.nexus_auth.config;

import com.leandro.nexus_auth.config.sentinel.SentinelProtectionFilter; // <--- NÃO ESQUEÇA DE IMPORTAR
import com.leandro.nexus_auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    // 1. INJETAMOS O FILTRO DO SENTINELA AQUI
    private final SentinelProtectionFilter sentinelProtectionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)

                // --- ORDEM DOS FILTROS (MUITO IMPORTANTE) ---

                // O JWT roda antes da autenticação padrão (UsernamePassword)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // O SENTINELA roda antes do JWT!
                // Motivo: Se for um ataque de SQL Injection ou DDOS, a gente bloqueia
                // antes mesmo de gastar processamento verificando token ou indo no banco.
                .addFilterBefore(sentinelProtectionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}