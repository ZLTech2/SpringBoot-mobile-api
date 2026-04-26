package com.negocionaarea.mobile_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.negocionaarea.mobile_api.dto.ClienteRequest;
import com.negocionaarea.mobile_api.dto.ClienteResponse;
import com.negocionaarea.mobile_api.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping
    public ClienteResponse salvar(@RequestBody ClienteRequest dto) {
        return service.salvar(dto);
    }

    @GetMapping("/listar")
    public List<ClienteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<ClienteResponse> getMe(JwtAuthenticationToken auth){
        return ResponseEntity.ok(service.getMe(auth.getToken().getSubject()));
    }
}