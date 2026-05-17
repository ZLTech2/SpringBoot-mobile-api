package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PromocaoRequest {
    private Double porcentagemDesconto;
    private LocalDate dataFinalPromocao;
    private Boolean gerarBanner;
}
