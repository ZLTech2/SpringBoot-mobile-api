package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.CupomResponse;
import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.CupomModel;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.dto.StatusCupom;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import com.negocionaarea.mobile_api.repository.CupomRepository;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CupomService {

    private final CupomRepository cupomRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;

    public CupomService(CupomRepository cupomRepository,
                        ClienteRepository clienteRepository,
                        EmpresaRepository empresaRepository) {
        this.cupomRepository = cupomRepository;
        this.clienteRepository = clienteRepository;
        this.empresaRepository = empresaRepository;
    }

    public void gerarCupomSeAniversario(ClienteModel cliente) {
        LocalDate hoje = LocalDate.now();

        if (cliente.getDataNascimento() == null) return;

        if (cliente.getDataNascimento().getMonth() == hoje.getMonth()
                && cliente.getDataNascimento().getDayOfMonth() == hoje.getDayOfMonth()) {

            var existente = cupomRepository.findByCliente_AndUtilizadoFalseAndAnoGerado(cliente, hoje.getYear());
            if (existente.isPresent()) return;

            CupomModel cupom = new CupomModel();
            cupom.setCliente(cliente);
            cupom.setUtilizado(false);
            cupom.setAnoGerado(hoje.getYear());
            cupom.setCodigo(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            cupom.setCriadoEm(LocalDateTime.now());
            cupom.setPercentualDesconto(0);
            cupom.setStatus(StatusCupom.ATIVO);
            cupom.setTipo("ANIVERSARIO");
            cupom.setValidadeAte(hoje.plusDays(7));

            cupomRepository.save(cupom);
        }
    }

    public CupomResponse getCupomDisponivel(String emailCliente) {
        ClienteModel cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        CupomModel cupom = cupomRepository.findByCliente_AndUtilizadoFalseAndAnoGerado(cliente, LocalDate.now().getYear())
                .orElseThrow(() -> new RuntimeException("Nenhum cupom disponível"));

        CupomResponse response = new CupomResponse();
        response.setId(cupom.getId());
        response.setUtilizado(cupom.getUtilizado());
        response.setValidadeAte(cupom.getValidadeAte());
        response.setCodigo(cupom.getCodigo());
        return response;
    }

    public Integer usarCupom(String emailCliente, UUID empresaId) {
        ClienteModel cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        EmpresaModel empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        CupomModel cupom = cupomRepository.findByCliente_AndUtilizadoFalseAndAnoGerado(cliente, LocalDate.now().getYear())
                .orElseThrow(() -> new RuntimeException("Nenhum cupom disponível"));

        if (cupom.getValidadeAte().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cupom expirado");
        }

        cupom.setUtilizado(true);
        cupom.setStatus(StatusCupom.USADO);
        cupom.setPercentualDesconto(empresa.getPercentualCupomAniversario());
        cupomRepository.save(cupom);

        return empresa.getPercentualCupomAniversario();
    }
}