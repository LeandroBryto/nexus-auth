package com.leandro.nexus_auth.model;

import com.leandro.nexus_auth.enums.PerfilUsuario;
import com.leandro.nexus_auth.enums.TipoPlano;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario")
    private UUID id;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "username",  length = 255)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email; // Mantemos para recuperação de senha

    @Column(name = "senha", nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false)
    private PerfilUsuario perfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "plano", nullable = false)
    private TipoPlano plano;

    @Column(name = "ativo")
    private boolean ativo;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultimo_login")
    private LocalDateTime dataUltimoLogin;

    @Column(name = "ip_ultimo_login")
    private String ipUltimoLogin;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        if (this.plano == null) this.plano = TipoPlano.FREE;
        if (this.perfil == null) this.perfil = PerfilUsuario.USUARIO;
        // Pode definir ativo = true se não quiser confirmação de e-mail agora
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    // AQUI ESTÁ A CHAVE: O sistema agora olha para o USERNAME
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return ativo; }
}