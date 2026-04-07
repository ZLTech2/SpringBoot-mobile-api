package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.dto.EmpresaCreateRequest;
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

    @PostMapping("/create")
    public ResponseEntity<EmpresaModel> create(@RequestBody EmpresaCreateRequest request) {
        return ResponseEntity.ok(empresaService.create(request));
    }

    @GetMapping("/findall")
    public ResponseEntity<List<EmpresaModel>> findAll() {
        return ResponseEntity.ok(empresaService.findAll());
    }
}