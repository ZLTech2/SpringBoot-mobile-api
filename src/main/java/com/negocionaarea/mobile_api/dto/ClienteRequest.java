package com.negocionaarea.mobile_api.dto;

import java.time.LocalDate;

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
    /**
     * Formato esperado no JSON: "YYYY-MM-DD" (ex: "1997-04-21").
     */
    private LocalDate dataNascimento;

    // endereço separado
    private EnderecoRequest endereco;
}
