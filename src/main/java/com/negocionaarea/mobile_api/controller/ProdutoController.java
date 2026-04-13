package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.dto.ProdutoCreateRequest;
import com.negocionaarea.mobile_api.dto.ProdutoResponse;
import com.negocionaarea.mobile_api.dto.ProdutoUpdateRequest;
import com.negocionaarea.mobile_api.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ENTERPRISE')")
    public ResponseEntity<ProdutoResponse> create(@RequestBody ProdutoCreateRequest request, JwtAuthenticationToken auth) {
        return ResponseEntity.ok(produtoService.create(request, auth.getToken().getSubject()));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> getAll() {
        return ResponseEntity.ok(produtoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(produtoService.getbyId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ENTERPRISE')")
    public ResponseEntity<Void> delete(@PathVariable UUID id, JwtAuthenticationToken auth) {
        produtoService.delete(id, auth.getToken().getSubject());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ENTERPRISE')")
    public ResponseEntity<ProdutoResponse> updateAll(@PathVariable UUID id, @RequestBody ProdutoUpdateRequest request, JwtAuthenticationToken auth) {
        return ResponseEntity.ok(produtoService.updateAll(id, request, auth.getToken().getSubject()));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ENTERPRISE')")
    public ResponseEntity<ProdutoResponse> update(@PathVariable UUID id, @RequestBody ProdutoUpdateRequest request, JwtAuthenticationToken auth) {
        return ResponseEntity.ok(produtoService.update(id, request, auth.getToken().getSubject()));
    }
}
