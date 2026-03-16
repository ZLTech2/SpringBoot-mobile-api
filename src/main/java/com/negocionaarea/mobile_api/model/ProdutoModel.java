package com.negocionaarea.mobile_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name  = "produtos")
public class ProdutoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idProduto;
    @Column(nullable = false, length = 100)
    private String descricaoProduto;
    @Column(nullable = false )
    private double precoProduto;
    @Column(nullable = false)
    private LocalDateTime dataCriacaoProduto;
    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private EmpresaModel empresa;
}
