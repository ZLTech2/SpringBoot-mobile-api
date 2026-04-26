package com.negocionaarea.mobile_api.controller;


import com.negocionaarea.mobile_api.dto.EmpresaRequest;
import com.negocionaarea.mobile_api.dto.EmpresaResponse;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.service.EmpresaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    public EmpresaResponse create(@RequestBody EmpresaRequest request) {
        return empresaService.create(request);
    }

    @GetMapping
    public List<EmpresaResponse> findAll() {
        return empresaService.findAll();
    }

    @GetMapping("me")
    @PreAuthorize("hasRole('ENTERPRISE')")
    public ResponseEntity<EmpresaResponse> getMe(JwtAuthenticationToken auth){
        return ResponseEntity.ok(empresaService.getMe(auth.getToken().getSubject()));
    }
}