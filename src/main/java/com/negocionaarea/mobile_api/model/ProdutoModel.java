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
    private String nome;
    @Column(nullable = false, length = 100)
    private String descricaoProduto;
    @Column(nullable = false )
    private Double precoProduto;
    @Column(nullable = false)
    private LocalDateTime dataPostagem = LocalDateTime.now();
    // Armazena uma URL/caminho publico para a imagem (ex: "/uploads/produtos/{id}/arquivo.jpg").
    // O upload em si pode ser feito via endpoint multipart e depois setado aqui.
    @Column(nullable = true, length = 1024)
    private String imagem;

    // Relação do produto com a entidade empresa
    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private EmpresaModel empresa;

    // Relação do produto com a entidade curtida
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<CurtidaModel>curtidas;
}
