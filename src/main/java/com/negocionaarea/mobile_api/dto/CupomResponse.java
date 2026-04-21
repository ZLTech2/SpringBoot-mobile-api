package com.negocionaarea.mobile_api.dto;

import com.negocionaarea.mobile_api.model.CupomStatus;
import com.negocionaarea.mobile_api.model.CupomTipo;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CupomResponse {
    private UUID id;
    private String codigo;
    private CupomTipo tipo;
    private CupomStatus status;
    private int percentualDesconto;
    private LocalDate validadeAte;
    private Instant criadoEm;
}

