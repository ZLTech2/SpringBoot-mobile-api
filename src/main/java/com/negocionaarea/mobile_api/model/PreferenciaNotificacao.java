package com.negocionaarea.mobile_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "PreferenciaNotificacao")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaNotificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private  Double longitudeCliente;
    @Column(nullable = false)
    private Double latitudeCliente;
    @Column(nullable = false)
    private Double raioMaximoKm;
    @Column(nullable = false)
    private List<String>categoriasInteresse;
}
