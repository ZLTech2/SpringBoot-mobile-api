package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.service.CurtidaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String>curtir(@@PathVariable UUID clienteId, @PathVariable UUID produtoId){
        String mensage = curtidaService.alternarCurtida(clienteId, produtoId);
        return ResponseEntity.ok(mensage);;
    }

    public ResponseEntity<List<ProdutoModel>>feedCurtidasCliente(@PathVariable UUID clienteId){
        return ResponseEntity.ok(curtidaService.listarPostsCurtidos(clienteId));
    }
}
