package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.dto.ProdutoCurtidoDto;
import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import com.negocionaarea.mobile_api.repository.CurtidaRepository;
import com.negocionaarea.mobile_api.service.CurtidaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/curtidas")
public class CurtidaController {

    private final CurtidaService curtidaService;
    private final ClienteRepository clienteRepository;
    private final CurtidaRepository curtidaRepository;

    public CurtidaController(CurtidaService curtidaService, ClienteRepository clienteRepository, CurtidaRepository curtidaRepository) {
        this.curtidaService = curtidaService;
        this.clienteRepository = clienteRepository;
        this.curtidaRepository = curtidaRepository;
    }

    @PostMapping("/produto/{produtoId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String>curtir(@PathVariable UUID produtoId, JwtAuthenticationToken auth){
        System.out.println("=== DEBUG CURTIDAs ===");

        // 1. Ver se auth chegou
        System.out.println("AUTH RECEBIDO: " + auth);

        // 2. Ver token bruto
        System.out.println("TOKEN: " + (auth != null ? auth.getToken() : "NULL"));

        // 3. Ver subject (email do usuário)
        String email = auth != null ? auth.getToken().getSubject() : null;
        System.out.println("EMAIL (SUBJECT): " + email);

        System.out.println("PRODUTO ID: " + produtoId);

        String mensagem = curtidaService.alternarCurtida(email, produtoId);

        return ResponseEntity.ok(mensagem);
    }

    @GetMapping("/produto/{produtoId}/status")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Boolean> verificarStatusCurtida(@PathVariable UUID produtoId, JwtAuthenticationToken auth) {
        String email = auth != null ? auth.getToken().getSubject() : null;

        if (email == null) {
            return ResponseEntity.status(401).body(false);
        }

        ClienteModel cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Usa o existsBy que criamos no seu Repository
        boolean jaCurtido = curtidaRepository.existsByClienteIdAndProdutoIdProduto(cliente.getId(), produtoId);

        return ResponseEntity.ok(jaCurtido);
    }

    @GetMapping("/feed")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ProdutoCurtidoDto>>feedCurtidasCliente(JwtAuthenticationToken auth){
        String email = auth.getToken().getSubject();

        List<ProdutoCurtidoDto> feed = curtidaService.listarPostsCurtidos(email);

        return ResponseEntity.ok(feed);
    }

    @GetMapping("/media")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Double> mediaCurtidas(
            @RequestParam UUID empresaId,
            @RequestParam String inicio,
            @RequestParam String fim
    ) {

        Double media = curtidaService.calcularMediaCurtidasPorProduto(
                empresaId,
                LocalDateTime.parse(inicio),
                LocalDateTime.parse(fim)
        );

        return ResponseEntity.ok(media);
    }
}
