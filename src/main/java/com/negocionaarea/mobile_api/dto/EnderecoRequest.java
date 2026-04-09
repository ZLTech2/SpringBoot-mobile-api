package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoRequest {

    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String cep;
}