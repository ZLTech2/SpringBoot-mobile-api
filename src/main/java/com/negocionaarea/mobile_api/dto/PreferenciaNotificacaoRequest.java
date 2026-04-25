package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PreferenciaNotificacaoRequest {
    private UUID clienteId;
    private Double raioMaximoKm;
    private List<String> categoriasInteresse;
    private boolean receberQualquerPromo;
}
