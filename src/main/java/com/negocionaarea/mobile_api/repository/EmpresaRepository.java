package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<EmpresaModel, UUID> {

    // Faz a busca para o login -tutu
    Optional<EmpresaModel> findByEmail(String email);

    // Verifica se o CNPJ já existe - tutu
    boolean existsByCnpj(String cnpj);

    // Verifica se o Email já existe - tutu
    boolean existsByEmail(String email);
}
