package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.CurtidaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurtidaRepository extends JpaRepository<CurtidaModel, UUID> {
    Optional<CurtidaModel>findByClienteIdAndProdutoIdProduto(UUID clienteId, UUID Idproduto);
    List<CurtidaModel> findAllByClienteIdOrderByDataHoraDesc(UUID clienteId);
}
