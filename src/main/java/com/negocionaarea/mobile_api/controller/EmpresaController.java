package com.negocionaarea.mobile_api.controller;

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

    @PostMapping
    public ResponseEntity<EmpresaModel> create(@RequestBody EmpresaModel empresa) {
        return ResponseEntity.ok(empresaService.create(empresa));
    }

    @GetMapping
    public ResponseEntity<List<EmpresaModel>> findAll() {
        return ResponseEntity.ok(empresaService.findAll());
    }
}