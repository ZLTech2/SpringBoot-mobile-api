package com.negocionaarea.mobile_api.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaCreateRequest {
    private String nome;
    private String cnpj;
    private String categoria;
    private String telefone;
    private String email;
    private String descricao;
    private String senha; //
    private EnderecoRequest endereco;
}
