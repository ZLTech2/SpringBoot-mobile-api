package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClienteRequest {

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private EnderecoRequest endereco;
    private LocalDate dataNascimento;

}