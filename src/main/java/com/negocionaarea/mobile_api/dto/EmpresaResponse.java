package com.negocionaarea.mobile_api.dto;

import com.negocionaarea.mobile_api.model.EnderecoModel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class EmpresaResponse {
    private UUID id;
    private Instant createdAt;
    private String nome;
    private String cnpj;
    private String categoria;
    private String telefone;
    private String email;
    private String descricao;
    private EnderecoResponse endereco;

}
