package com.negocionaarea.mobile_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoResponse {
    private String rua;
    private int numero;
    private String bairro;
    private String cidade;
    private String cep;
    private String estado;
}
