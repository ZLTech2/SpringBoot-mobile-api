package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.LoginRequest;
import com.negocionaarea.mobile_api.dto.LoginResponse;
import com.negocionaarea.mobile_api.dto.Role;
import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuthService {
    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long ttlSeconds;

    public AuthService(
            EmpresaRepository empresaRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.issuer:mobile-api}") String issuer,
            @Value("${app.jwt.ttl-seconds:3600}") long ttlSeconds
    ) {
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getSenha() == null || request.getTipo() == null) {
            throw new IllegalArgumentException("Email, senha e tipo sao obrigatorios");
        }

        String tipo = request.getTipo().trim().toLowerCase();
        String email = request.getEmail().trim().toLowerCase();
        List<String> roles;

        if ("empresa".equals(tipo)) {
            EmpresaModel empresa = empresaRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Credenciais invalidas"));
            if (!passwordEncoder.matches(request.getSenha(), empresa.getSenha())) {
                throw new IllegalArgumentException("Credenciais invalidas");
            }
            roles = List.of("ROLE_" + Role.ENTERPRISE.name());
        } else if ("cliente".equals(tipo)) {
            ClienteModel cliente = clienteRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Credenciais invalidas"));
            if (!passwordEncoder.matches(request.getSenha(), cliente.getSenha())) {
                throw new IllegalArgumentException("Credenciais invalidas");
            }
            roles = List.of("ROLE_" + Role.CUSTOMER.name());
        } else {
            throw new IllegalArgumentException("Tipo invalido. Use 'empresa' ou 'cliente'");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ttlSeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(email)
                .claim("tipo", tipo)
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new LoginResponse(token, tipo, expiresAt);
    }
}
