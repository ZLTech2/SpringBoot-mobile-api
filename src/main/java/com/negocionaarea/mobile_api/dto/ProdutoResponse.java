package com.negocionaarea.mobile_api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProdutoResponse(
        UUID id,
        String nome,
        String descricaoProduto,
        Double precoProduto,
        LocalDateTime dataPostagem,
        String imagem,
        UUID empresaId
) {
}

