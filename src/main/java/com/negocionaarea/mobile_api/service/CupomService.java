package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.CupomResponse;
import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.CupomModel;
import com.negocionaarea.mobile_api.model.CupomStatus;
import com.negocionaarea.mobile_api.model.CupomTipo;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import com.negocionaarea.mobile_api.repository.CupomRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CupomService {
    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private final CupomRepository cupomRepository;
    private final ClienteRepository clienteRepository;
    private final int percentualAniversario;
    private final int diasValidadeAniversario;

    public CupomService(
            CupomRepository cupomRepository,
            ClienteRepository clienteRepository,
            @Value("${app.cupom.aniversario.percentual:10}") int percentualAniversario,
            @Value("${app.cupom.aniversario.dias-validade:7}") int diasValidadeAniversario
    ) {
        this.cupomRepository = cupomRepository;
        this.clienteRepository = clienteRepository;
        this.percentualAniversario = percentualAniversario;
        this.diasValidadeAniversario = diasValidadeAniversario;
    }

    public List<CupomResponse> listarCuponsAtivosDoClientePorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario nao autenticado");
        }

        ClienteModel cliente = clienteRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));

        return cupomRepository.findByClienteIdAndStatusOrderByCriadoEmDesc(cliente.getId(), CupomStatus.ATIVO)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void emitirCupomAniversarioParaClienteSeElegivel(ClienteModel cliente, LocalDate hoje) {
        if (cliente == null || cliente.getId() == null || cliente.getDataNascimento() == null) {
            return;
        }
        if (hoje == null) {
            hoje = LocalDate.now();
        }
        if (!ehAniversario(cliente.getDataNascimento(), hoje)) {
            return;
        }

        int ano = hoje.getYear();
        UUID clienteId = cliente.getId();
        if (cupomRepository.existsByClienteIdAndTipoAndAnoGerado(clienteId, CupomTipo.ANIVERSARIO, ano)) {
            return;
        }

        CupomModel cupom = new CupomModel();
        cupom.setCliente(cliente);
        cupom.setTipo(CupomTipo.ANIVERSARIO);
        cupom.setStatus(CupomStatus.ATIVO);
        cupom.setAnoGerado(ano);
        cupom.setPercentualDesconto(percentualAniversario);
        cupom.setValidadeAte(hoje.plusDays(diasValidadeAniversario));
        cupom.setCriadoEm(Instant.now());
        cupom.setCodigo(gerarCodigoUnico("ANIV"));

        cupomRepository.save(cupom);
    }

    @Transactional
    public int emitirCuponsAniversarioDoDia(LocalDate hoje) {
        if (hoje == null) {
            hoje = LocalDate.now();
        }

        int mes = hoje.getMonthValue();
        int dia = hoje.getDayOfMonth();
        int ano = hoje.getYear();

        List<ClienteModel> aniversariantes = new ArrayList<>(clienteRepository.findAniversariantesDoDia(mes, dia));

        // Para nascidos em 29/02, emite em 28/02 quando o ano nao e bissexto.
        if (mes == 2 && dia == 28 && !hoje.isLeapYear()) {
            aniversariantes.addAll(clienteRepository.findAniversariantesDoDia(2, 29));
        }

        int emitidos = 0;
        for (ClienteModel cliente : aniversariantes) {
            UUID clienteId = cliente.getId();
            if (clienteId == null) {
                continue;
            }
            if (cupomRepository.existsByClienteIdAndTipoAndAnoGerado(clienteId, CupomTipo.ANIVERSARIO, ano)) {
                continue;
            }

            emitirCupomAniversarioParaClienteSeElegivel(cliente, hoje);
            emitidos++;
        }

        return emitidos;
    }

    private CupomResponse toResponse(CupomModel cupom) {
        CupomResponse r = new CupomResponse();
        r.setId(cupom.getId());
        r.setCodigo(cupom.getCodigo());
        r.setTipo(cupom.getTipo());
        r.setStatus(cupom.getStatus());
        r.setPercentualDesconto(cupom.getPercentualDesconto());
        r.setValidadeAte(cupom.getValidadeAte());
        r.setCriadoEm(cupom.getCriadoEm());
        return r;
    }

    private String gerarCodigoUnico(String prefixo) {
        String p = (prefixo == null ? "" : prefixo.trim().toUpperCase());

        for (int i = 0; i < 20; i++) {
            String codigo = p + "-" + randomToken(10);
            if (!cupomRepository.existsByCodigo(codigo)) {
                return codigo;
            }
        }
        // Fallback: baixa probabilidade de colisao, mas garante uma saida.
        return p + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private boolean ehAniversario(LocalDate nascimento, LocalDate hoje) {
        if (nascimento == null || hoje == null) {
            return false;
        }
        int mesNasc = nascimento.getMonthValue();
        int diaNasc = nascimento.getDayOfMonth();

        // 29/02: emite em 28/02 quando o ano nao e bissexto.
        if (mesNasc == 2 && diaNasc == 29 && !hoje.isLeapYear()) {
            return hoje.getMonthValue() == 2 && hoje.getDayOfMonth() == 28;
        }

        return hoje.getMonthValue() == mesNasc && hoje.getDayOfMonth() == diaNasc;
    }

    private String randomToken(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHANUM[RNG.nextInt(ALPHANUM.length)]);
        }
        return sb.toString();
    }
}
