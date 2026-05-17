package com.negocionaarea.mobile_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoCurtidoDto {
    private UUID id;
    private String nome;
    private String descricao;
    private String imagem;
    private Double precoProduto;
    private Boolean isLiked = true;

}
