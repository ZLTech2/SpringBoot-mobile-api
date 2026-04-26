package com.negocionaarea.mobile_api.model;

import com.negocionaarea.mobile_api.dto.StatusCupom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cupons")
@Getter
@Setter
public class CupomModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ano_gerado", nullable = false)
    private Integer anoGerado;

    @Column(name = "codigo", nullable = false, unique = true)
    private String codigo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "percentual_desconto", nullable = false)
    private Integer percentualDesconto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusCupom status = StatusCupom.ATIVO;

    @Column(name = "tipo", nullable = false)
    private String tipo = "ANIVERSARIO";

    @Column(name = "validade_ate", nullable = false)
    private LocalDate validadeAte;

    @Column(name = "utilizado", nullable = false)
    private Boolean utilizado = false;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private ClienteModel cliente;
}