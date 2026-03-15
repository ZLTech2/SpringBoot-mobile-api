package com.negocionaarea.mobile_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.negocionaarea.mobile_api.model.ProdutoModel;


public interface ProdutoRepository  extends JpaRepository<ProdutoModel, Long> {
}
