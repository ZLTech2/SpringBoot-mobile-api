package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.LoginRequest;
import com.negocionaarea.mobile_api.dto.LoginResponse;
import com.negocionaarea.mobile_api.dto.LoginCredentialsRequest;
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
import java.util.Optional;

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

        return login(request.getEmail(), request.getSenha(), request.getTipo());
    }

    public LoginResponse loginCliente(LoginCredentialsRequest request) {
        if (request == null || request.getEmail() == null || request.getSenha() == null) {
            throw new IllegalArgumentException("Email e senha sao obrigatorios");
        }
        return login(request.getEmail(), request.getSenha(), "cliente");
    }

    public LoginResponse loginEmpresa(LoginCredentialsRequest request) {
        if (request == null || request.getEmail() == null || request.getSenha() == null) {
            throw new IllegalArgumentException("Email e senha sao obrigatorios");
        }
        return login(request.getEmail(), request.getSenha(), "empresa");
    }

    public LoginResponse loginAuto(LoginCredentialsRequest request){
        if(request == null || request.getEmail() == null || request.getSenha()== null){
            throw new IllegalArgumentException("Email, senha são obrigatorios");
        }

        String email = request.getEmail().trim().toLowerCase();
        String senha = request.getSenha();

        System.out.println(">>> EMAIL RECEBIDO: " + email);
        System.out.println(">>> SENHA RECEBIDA: " + senha);

        Optional<EmpresaModel> empresa = empresaRepository.findByEmail(email);
        System.out.println(">>> EMPRESA ENCONTRADA: " + empresa.isPresent());

        if (empresa.isPresent()) {
            boolean senhaOk = passwordEncoder.matches(senha, empresa.get().getSenha());
            System.out.println(">>> SENHA BATE: " + senhaOk);
            if (!senhaOk) {
                throw new IllegalArgumentException("Credenciais invalidas");
            }
            return login(email, senha, "empresa");
        }

        Optional<ClienteModel> cliente = clienteRepository.findByEmail(email);
        System.out.println(">>> CLIENTE ENCONTRADO: " + cliente.isPresent());

        if (cliente.isPresent()) {
            boolean senhaOk = passwordEncoder.matches(senha, cliente.get().getSenha());
            System.out.println(">>> SENHA BATE: " + senhaOk);
            if (!senhaOk) {
                throw new IllegalArgumentException("Credenciais invalidas");
            }
            return login(email, senha, "cliente");
        }

        throw new IllegalArgumentException("Credenciais invalidas");
    }


    public LoginResponse login(String emailRaw, String senhaRaw, String tipoRaw) {
        if (emailRaw == null || senhaRaw == null || tipoRaw == null) {
            throw new IllegalArgumentException("Email, senha e tipo sao obrigatorios");
        }

        String tipo = tipoRaw.trim().toLowerCase();
        String email = emailRaw.trim().toLowerCase();
        List<String> roles;

        if ("empresa".equals(tipo)) {
            EmpresaModel empresa = empresaRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Credenciais invalidas"));
            if (!passwordEncoder.matches(senhaRaw, empresa.getSenha())) {
                throw new IllegalArgumentException("Credenciais invalidas");
            }
            roles = List.of("ROLE_" + Role.ENTERPRISE.name());
        } else if ("cliente".equals(tipo)) {
            ClienteModel cliente = clienteRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Credenciais invalidas"));
            if (!passwordEncoder.matches(senhaRaw, cliente.getSenha())) {
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

