package com.negocionaarea.mobile_api.controller;

import java.util.List;

import com.negocionaarea.mobile_api.dto.EmpresaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.negocionaarea.mobile_api.dto.ClienteRequest;
import com.negocionaarea.mobile_api.dto.ClienteResponse;
import com.negocionaarea.mobile_api.service.ClienteService;
import org.springframework.web.multipart.MultipartFile;

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
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ClienteResponse> getMe(JwtAuthenticationToken auth){
        return ResponseEntity.ok(service.getMe(auth.getToken().getSubject()));
    }

    @PostMapping(value = "/me/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ClienteResponse> uploadLogo(@RequestParam("logo") MultipartFile logo, JwtAuthenticationToken auth){
        return ResponseEntity.ok(service.uploadLogo(logo, auth.getToken().getSubject()));
    }
}