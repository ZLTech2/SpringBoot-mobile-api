package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.DashboardAnalyticsDto;
import com.negocionaarea.mobile_api.dto.DashboardAnalyticsDto.*;
import com.negocionaarea.mobile_api.repository.CurtidaRepository;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DashboardService {

    private final CurtidaRepository curtidaRepository;
    private final EmpresaRepository empresaRepository;

    private static final Map<Integer, String> DIA_SEMANA_MAP = Map.of(
            1, "Dom",
            2, "Seg",
            3, "Ter",
            4, "Qua",
            5, "Qui",
            6, "Sex",
            7, "Sáb"
            );

    public DashboardService(CurtidaRepository curtidaRepository, EmpresaRepository empresaRepository){
        this.curtidaRepository = curtidaRepository;
        this.empresaRepository = empresaRepository;
    }

    /**
     * Gera o analytics completo para uma empresa autenticada.
     *
     * @param emailEmpresa email extraído do JWT
     * @param periodo      "7dias" | "30dias" | "mes" | "ano" (default: "mes")
     */

    public DashboardAnalyticsDto gerarAnalytics(String emailEmpresa, String periodo) {

        EmpresaModel empresa = empresaRepository.findByEmail(emailEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        UUID empresaId = empresa.getId();

        // Calcular janelas de tempo
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicio = calcularInicio(periodo, agora);
        LocalDateTime fim    = agora;

        // Janela do mês anterior para variação %
        YearMonth mesAtual    = YearMonth.from(agora);
        YearMonth mesAnterior = mesAtual.minusMonths(1);

        LocalDateTime inicioMesAtual    = mesAtual.atDay(1).atStartOfDay();
        LocalDateTime fimMesAtual       = mesAtual.atEndOfMonth().atTime(23, 59, 59);
        LocalDateTime inicioMesAnterior = mesAnterior.atDay(1).atStartOfDay();
        LocalDateTime fimMesAnterior    = mesAnterior.atEndOfMonth().atTime(23, 59, 59);

        // ── Métricas principais ───────────────────────────────────────────────

        Long totalCurtidas       = curtidaRepository.countTotalByEmpresaId(empresaId);
        Long curtidasMesAtual    = curtidaRepository.countByEmpresaIdAndPeriodo(empresaId, inicioMesAtual,    fimMesAtual);
        Long curtidasMesAnterior = curtidaRepository.countByEmpresaIdAndPeriodo(empresaId, inicioMesAnterior, fimMesAnterior);

        double variacao = 0.0;
        if (curtidasMesAnterior != null && curtidasMesAnterior > 0) {
            variacao = ((curtidasMesAtual - curtidasMesAnterior) * 100.0) / curtidasMesAnterior;
        }

        Double mediaPorProduto = curtidaRepository.mediaCurtidasPorProduto(empresaId, inicio, fim);

        // ── Top produtos ──────────────────────────────────────────────────────

        List<Object[]> rawTop = curtidaRepository.topProdutosPorCurtidas(empresaId, inicio, fim);
        List<TopProdutoDto> topProdutos = new ArrayList<>();
        String publicacaoMaisCurtida = "—";
        Long   curtidasTop           = 0L;

        for (Object[] row : rawTop) {
            String nome     = (String) row[0];
            Long   curtidas = ((Number) row[1]).longValue();
            String imagem   = (String) row[2];
            topProdutos.add(new TopProdutoDto(nome, curtidas, imagem));
        }
        if (!topProdutos.isEmpty()) {
            publicacaoMaisCurtida = topProdutos.get(0).getNome();
            curtidasTop           = topProdutos.get(0).getCurtidas();
        }

        // ── Curtidas por dia ─────────────────────────────────────────────────

        List<Object[]> rawDia = curtidaRepository.countPorDia(empresaId, inicio, fim);
        List<CurtidasPorDiaDto> porDia = new ArrayList<>();
        DateTimeFormatter fmtDia = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Object[] row : rawDia) {
            String data  = row[0].toString();
            Long   total = ((Number) row[1]).longValue();
            porDia.add(new CurtidasPorDiaDto(data, total));
        }

        // ── Curtidas por hora ─────────────────────────────────────────────────

        List<Object[]> rawHora = curtidaRepository.countPorHora(empresaId, inicio, fim);
        List<CurtidasPorHoraDto> porHora = new ArrayList<>();

        for (Object[] row : rawHora) {
            Integer hora  = ((Number) row[0]).intValue();
            Long    total = ((Number) row[1]).longValue();
            porHora.add(new CurtidasPorHoraDto(hora, total));
        }

        // ── Curtidas por dia da semana ────────────────────────────────────────

        List<Object[]> rawSemana = curtidaRepository.countPorDiaSemana(empresaId, inicio, fim);
        List<CurtidasPorDiaSemanDto> porDiaSemana = new ArrayList<>();

        for (Object[] row : rawSemana) {
            Integer dow   = ((Number) row[0]).intValue();
            Long    total = ((Number) row[1]).longValue();
            String  label = DIA_SEMANA_MAP.getOrDefault(dow, "?");
            porDiaSemana.add(new CurtidasPorDiaSemanDto(label, total));
        }

        // ── Curtidas por bairro ───────────────────────────────────────────────

        List<Object[]> rawBairro = curtidaRepository.countPorBairro(empresaId, inicio, fim);
        List<CurtidasPorBairroDto> porBairro = new ArrayList<>();

        for (Object[] row : rawBairro) {
            String bairro = (String) row[0];
            Long   total  = ((Number) row[1]).longValue();
            porBairro.add(new CurtidasPorBairroDto(bairro, total));
        }

        // ── Montar DTO final ──────────────────────────────────────────────────

        DashboardAnalyticsDto dto = new DashboardAnalyticsDto();
        dto.setTotalCurtidas(totalCurtidas != null ? totalCurtidas : 0L);
        dto.setCurtidasMesAtual(curtidasMesAtual != null ? curtidasMesAtual : 0L);
        dto.setCurtidasMesAnterior(curtidasMesAnterior != null ? curtidasMesAnterior : 0L);
        dto.setVariacaoPercentual(Math.round(variacao * 10.0) / 10.0);
        dto.setPublicacaoMaisCurtida(publicacaoMaisCurtida);
        dto.setCurtidasPublicacaoTop(curtidasTop);
        dto.setMediaPorPublicacao(mediaPorProduto != null ? Math.round(mediaPorProduto * 10.0) / 10.0 : 0.0);
        dto.setCurtidasPorDia(porDia);
        dto.setCurtidasPorHora(porHora);
        dto.setCurtidasPorDiaSemana(porDiaSemana);
        dto.setCurtidasPorBairro(porBairro);
        dto.setTopProdutos(topProdutos);

        return dto;
    }

    private LocalDateTime calcularInicio(String periodo, LocalDateTime agora) {
        return switch (periodo == null ? "mes" : periodo.toLowerCase()) {
            case "7dias"  -> agora.minusDays(7);
            case "30dias" -> agora.minusDays(30);
            case "ano"    -> agora.withDayOfYear(1).toLocalDate().atStartOfDay();
            default       -> YearMonth.from(agora).atDay(1).atStartOfDay(); // "mes"
        };
    }
}
