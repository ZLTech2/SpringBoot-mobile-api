package com.negocionaarea.mobile_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
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
    @Column(nullable = false, length = 255)
    private String nomeProduto;
    @Column(nullable = false, length = 100)
    private String descricaoProduto;
    @Column(nullable = false )
    private double precoProduto;
    @Column(nullable = false, updatable = false) //updatetable deixa fazer o insert mas depois o valor nunca mais se altera
    @CreationTimestamp
    private LocalDateTime dataCriacaoProduto;
    @Column(nullable = false)
    private boolean isPromocao = false;

    // Relação do produto com a entidade empresa
    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private EmpresaModel empresa;

    // Relação do produto com a entidade curtida
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<CurtidaModel>curtidas;
}
