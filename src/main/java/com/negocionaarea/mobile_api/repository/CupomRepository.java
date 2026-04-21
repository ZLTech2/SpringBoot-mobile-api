package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.CupomModel;
import com.negocionaarea.mobile_api.model.CupomStatus;
import com.negocionaarea.mobile_api.model.CupomTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CupomRepository extends JpaRepository<CupomModel, UUID> {
    boolean existsByCodigo(String codigo);

    boolean existsByClienteIdAndTipoAndAnoGerado(UUID clienteId, CupomTipo tipo, int anoGerado);

    List<CupomModel> findByClienteIdAndStatusOrderByCriadoEmDesc(UUID clienteId, CupomStatus status);
}

