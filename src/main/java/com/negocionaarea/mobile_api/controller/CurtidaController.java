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
        String email = auth.getToken().getSubject();

        String mensage = curtidaService.alternarCurtida(email, produtoId);

        return ResponseEntity.ok(mensage);
    }

    @GetMapping("/feed")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ProdutoModel>>feedCurtidasCliente(JwtAuthenticationToken auth){
        String email = auth.getToken().getSubject();

        return ResponseEntity.ok(curtidaService.listarPostsCurtidos(email));
    }
}
