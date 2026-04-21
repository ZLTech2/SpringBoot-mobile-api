package com.negocionaarea.mobile_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "cupons",
        indexes = {
                @Index(name = "idx_cupom_cliente", columnList = "id_cliente"),
                @Index(name = "idx_cupom_cliente_tipo_ano", columnList = "id_cliente,tipo,ano_gerado")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cupom_codigo", columnNames = "codigo")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CupomModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteModel cliente;

    @Column(nullable = false, length = 32)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CupomTipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CupomStatus status;

    /**
     * Evita emitir mais de um cupom do mesmo tipo por ano, por cliente.
     */
    @Column(name = "ano_gerado", nullable = false)
    private int anoGerado;

    /**
     * Percentual de desconto (ex: 10 = 10%).
     */
    @Column(name = "percentual_desconto", nullable = false)
    private int percentualDesconto;

    @Column(name = "validade_ate", nullable = false)
    private LocalDate validadeAte;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
}
