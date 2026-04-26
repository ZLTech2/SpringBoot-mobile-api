package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CupomResponse {
    private UUID id;
    private Boolean utilizado;
    private LocalDate validadeAte;
    private String codigo;
}