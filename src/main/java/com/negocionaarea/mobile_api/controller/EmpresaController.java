package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.dto.EmpresaRequest;
import com.negocionaarea.mobile_api.dto.EmpresaResponse;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.service.EmpresaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping("/cadastrar")
    public EmpresaResponse create(@RequestBody EmpresaRequest request) {
        return empresaService.create(request);
    }

    @GetMapping("/listar")
    public List<EmpresaResponse> findAll() {
        return empresaService.findAll();
    }
}