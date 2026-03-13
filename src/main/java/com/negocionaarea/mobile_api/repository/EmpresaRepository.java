package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<EmpresaModel, UUID> {

    Optional <EmpresaModel> findByEmail(String email);
}
