package com.negocionaarea.mobile_api.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CupomAniversarioScheduler {
    private final CupomService cupomService;

    public CupomAniversarioScheduler(CupomService cupomService) {
        this.cupomService = cupomService;
    }

    /**
     * Roda diariamente e emite cupons para clientes que fazem aniversario no dia.
     * Cron configuravel via app.cupom.aniversario.cron.
     */
    @Scheduled(cron = "${app.cupom.aniversario.cron:0 5 0 * * *}")
    public void emitirCuponsAniversario() {
        cupomService.emitirCuponsAniversarioDoDia(LocalDate.now());
    }
}

