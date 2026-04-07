package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.ClienteModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Login de Empresa
    @PostMapping("/login/empresa")
    public ResponseEntity<String> loginEmpresa(@RequestBody EmpresaModel dados) {
        // O Spring Security faz a validação por trás, aqui retornamos o sucesso
        return ResponseEntity.ok("Login de Empresa realizado! Bem-vindo: " + dados.getEmail());
    }

    // Login de Cliente
    @PostMapping("/login/cliente")
    public ResponseEntity<String> loginCliente(@RequestBody ClienteModel dados) {
        return ResponseEntity.ok("Login de Cliente realizado! Bem-vindo: " + dados.getEmail());
    }
}