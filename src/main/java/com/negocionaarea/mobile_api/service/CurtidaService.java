package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.CurtidaModel;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import com.negocionaarea.mobile_api.repository.CurtidaRepository;
import com.negocionaarea.mobile_api.repository.ProdutoRepository;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CurtidaService {
    private final CurtidaRepository curtidaRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    public CurtidaService(CurtidaRepository curtidaRepository, ProdutoRepository produtoRepository, ClienteRepository clienteRepository) {
        this.curtidaRepository = curtidaRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public String alternarCurtida (UUID clienteId, UUID produtoId){

        Optional<CurtidaModel> curtidaExistente = curtidaRepository.findByClienteIdAndProdutoId(clienteId, produtoId);

        if(curtidaExistente.isPresent()){
            curtidaRepository.delete(curtidaExistente.get());
            //diminui o contator
            produtoRepository.decrementarCurtida(produtoId);

            return "Curtida removida";
        }else{
            ClienteModel cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

            ProdutoModel produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            CurtidaModel curtida = new CurtidaModel();
            curtida.setCliente(cliente);
            curtida.setProduto(produto);

            curtidaRepository.save(curtida);
            produtoRepository.incrementarCurtida(produtoId);

            return "Produto curtido com sucesso";

        }
    }

    public List<ProdutoModel> listarPostsCurtidos(UUID clienteId){
        return curtidaRepository.findAllByClienteIdOrderByDataHoraDesc(clienteId)
                .stream()
                .map(CurtidaModel::getProduto)
                .distinct()
                .collect(Collectors.toList());
    }


}
