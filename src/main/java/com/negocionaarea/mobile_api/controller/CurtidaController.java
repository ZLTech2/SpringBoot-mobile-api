package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.service.CurtidaService;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{clienteId}/produto/{produtoId}")
    public ResponseEntity<String>curtir(@PathVariable UUID clienteId, @PathVariable UUID produtoId){
        String mensage = curtidaService.alternarCurtida(clienteId, produtoId);
        return ResponseEntity.ok(mensage);
    }

    @GetMapping("/feed/{clienteId}")
    public ResponseEntity<List<ProdutoModel>>feedCurtidasCliente(@PathVariable UUID clienteId){
        return ResponseEntity.ok(curtidaService.listarPostsCurtidos(clienteId));
    }
}
