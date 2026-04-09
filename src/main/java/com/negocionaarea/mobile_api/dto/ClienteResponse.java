package com.negocionaarea.mobile_api.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteResponse {

    private UUID id;
    private String nomeCliente;
    private String email;
    private String urlPerfilCliente;
    private String telefone;
    private EnderecoRequest endereco;
}