package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.dto.PreferenciaNotificacaoRequest;
import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import com.negocionaarea.mobile_api.service.PreferenciaNotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificacao")
public class PreferenciaNotificacaoController {

    private final PreferenciaNotificacaoService service;

    public PreferenciaNotificacaoController(PreferenciaNotificacaoService service) {
        this.service = service;
    }

    public ResponseEntity<PreferenciaNotificacaoModel> criar(@RequestBody PreferenciaNotificacaoRequest request){
        return ResponseEntity.ok(service.salvar(request));
    }
}
