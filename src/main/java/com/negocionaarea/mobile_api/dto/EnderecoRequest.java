package com.negocionaarea.mobile_api.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoRequest {
    private String cep;
    private String rua;
    private int numero;
    private String cidade;
    private String estado;
    private String bairro;
}
