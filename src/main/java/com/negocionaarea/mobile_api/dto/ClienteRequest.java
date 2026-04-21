package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {

    private String nome;
    private String email;
    private String senha;
    private String urlPerfil;
    private String telefone;

    // endereço separado
    private EnderecoRequest endereco;
}