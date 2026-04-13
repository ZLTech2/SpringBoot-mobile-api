package com.negocionaarea.mobile_api.dto;

import java.util.UUID;

public record ProdutoCreateRequest(
        String nome,
        String descricaoProduto,
        Double precoProduto,
        String imagem,
        UUID empresaId
) {
}

