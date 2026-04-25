package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.dto.PreferenciaNotificacaoRequest;
import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import com.negocionaarea.mobile_api.service.PreferenciaNotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/configuracao")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PreferenciaNotificacaoModel> criar(@RequestBody PreferenciaNotificacaoRequest request, JwtAuthenticationToken auth){
        //pega o usário do token
        String email = auth.getToken().getSubject();

        PreferenciaNotificacaoModel response = service.salvar(request, email);

        return ResponseEntity.ok(response);
    }
}
