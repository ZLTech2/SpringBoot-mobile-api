package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.dto.CupomResponse;
import com.negocionaarea.mobile_api.service.CupomService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cupons")
public class CupomController {

    private final CupomService cupomService;

    public CupomController(CupomService cupomService) {
        this.cupomService = cupomService;
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<CupomResponse> meusCupons(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt == null ? null : jwt.getSubject();
        return cupomService.listarCuponsAtivosDoClientePorEmail(email);
    }
}

