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

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    // Relação da curtida com a entidade produto
    @ManyToOne
    @JoinColumn(name = "id_produto")
    private ProdutoModel produto;

    // Relação da curtida com a entidade cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private ClienteModel cliente;
}
