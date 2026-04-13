package com.negocionaarea.mobile_api.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoModel {
    @Column(length = 9)
    private String cep;
    @Column(length = 100)
    private String rua;
    @Column
    private int numero;
    @Column(length = 100)
    private String cidade;
    @Column(length = 100)
    private String estado;
    @Column(length = 100)
    private String bairro;
}
