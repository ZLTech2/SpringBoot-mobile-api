package com.negocionaarea.mobile_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "curtida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurtidaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idCurtida;
    @CreationTimestamp
    @Column(nullable = false, updatable = false) //updatetable deixa fazer o insert mas depois o valor nunca mais se altera
    private LocalDateTime dataCurtida;

    // Relação da curtida com a entidade produto
    @ManyToOne
    @JoinColumn(name = "id_produto")
    private ProdutoModel post;

    // Relação da curtida com a entidade cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private ClienteModel cliente;
}
