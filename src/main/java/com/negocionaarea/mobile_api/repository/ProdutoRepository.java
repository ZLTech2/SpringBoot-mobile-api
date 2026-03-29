package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.ProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<ProdutoModel, UUID> {
}
