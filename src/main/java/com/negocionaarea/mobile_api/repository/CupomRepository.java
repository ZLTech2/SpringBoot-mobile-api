package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.CupomModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CupomRepository extends JpaRepository<CupomModel, UUID> {

    Optional<CupomModel> findByCliente_AndUtilizadoFalseAndAnoGerado(ClienteModel cliente, Integer anoGerado);
}
