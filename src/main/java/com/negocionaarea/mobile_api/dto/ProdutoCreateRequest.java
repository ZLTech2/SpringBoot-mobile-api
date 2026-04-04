package com.negocionaarea.mobile_api.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record ProdutoCreateRequest(

        @NotBlank(message = "o Nome é obrigatório")
        String nome,

        @NotBlank(message = "Forneça uma descrição para o produto")
        String descricaoProduto,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser um valor positivo")
        Double precoProduto,

        @NotBlank(message = "A imagem é obrigatória")
        String imagem,
        UUID empresaId
) {
}

