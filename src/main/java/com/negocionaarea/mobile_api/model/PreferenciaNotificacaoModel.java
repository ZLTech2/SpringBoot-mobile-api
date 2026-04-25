package com.negocionaarea.mobile_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "preferencia_notificacao")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaNotificacaoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private Double raioMaximoKm;
    @ElementCollection
    @CollectionTable(name = "preferencia_categorias", joinColumns = @JoinColumn(name = "preferencia_id"))
    @Column(name = "categoria", nullable = false)
    private List<String>categoriasInteresse;
    @Column(nullable = true)
    private boolean receberQualquerPromo = false;

    // mapeamento da entidade cliente e PreferenciaNotificacao
    @OneToOne
    @JoinColumn(name = "cliente_id")
    private ClienteModel cliente;
}
