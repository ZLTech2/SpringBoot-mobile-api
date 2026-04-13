package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {

    private String nomeCliente;
    private String email;
    private String senhaCliente;
    private String urlPerfilCliente;
    private String telefone;

    // endereço separado
    private EnderecoRequest endereco;
}