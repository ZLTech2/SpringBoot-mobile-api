package com.negocionaarea.mobile_api.controller;


import com.negocionaarea.mobile_api.dto.CupomResponse;
import com.negocionaarea.mobile_api.dto.CupomUsarRequest;
import com.negocionaarea.mobile_api.service.CupomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cupons")
public class CupomController {

    private final CupomService cupomService;

    public CupomController(CupomService cupomService){
        this.cupomService = cupomService;
    }

    @GetMapping("/meu")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CupomResponse> getMeuCupom(JwtAuthenticationToken auth){
        return ResponseEntity.ok(cupomService.getCupomDisponivel(auth.getToken().getSubject()));
    }

    @PostMapping("/usar")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Integer> usarCupom(@RequestBody CupomUsarRequest dto, JwtAuthenticationToken auth) {
        Integer desconto = cupomService.usarCupom(auth.getToken().getSubject(), dto.getEmpresaId());
        return ResponseEntity.ok(desconto);
    }
}
