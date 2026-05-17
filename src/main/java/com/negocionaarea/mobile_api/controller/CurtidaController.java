package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.service.CurtidaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/curtidas")
public class CurtidaController {

    private final CurtidaService curtidaService;

    public CurtidaController(CurtidaService curtidaService) {
        this.curtidaService = curtidaService;
    }

    @PostMapping("/produto/{produtoId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String>curtir(@PathVariable UUID produtoId, JwtAuthenticationToken auth){
        System.out.println("=== DEBUG CURTIDA ===");

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

    @GetMapping("/feed")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ProdutoModel>>feedCurtidasCliente(JwtAuthenticationToken auth){
        String email = auth.getToken().getSubject();

        return ResponseEntity.ok(curtidaService.listarPostsCurtidos(email));
    }
}
