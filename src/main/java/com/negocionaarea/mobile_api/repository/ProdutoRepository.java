package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface ProdutoRepository  extends JpaRepository<ProdutoModel, UUID> {
    List<ProdutoModel> findByEmpresa(EmpresaModel empresa);
    @Modifying // Indica que a query vai alterar dados
    @Query("UPDATE ProdutoModel p SET p.contadorCurtidas = p.contadorCurtidas + 1 WHERE p.idProduto = :id")
    void incrementarCurtida(Long id);

    @Modifying
    @Query("UPDATE ProdutoModel p SET p.contadorCurtidas = p.contadorCurtidas - 1 WHERE p.idProduto = :id")
    void decrementarCurtida(Long id);
}
