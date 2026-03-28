package com.negocionaarea.mobile_api.model;

import com.negocionaarea.mobile_api.dto.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, length = 100)
    private String nomeCliente;
    @Column(nullable = false, length = 50)
    private String emailCliente;
    @Column(nullable = false, length = 100)
    private String senhaCliente;
    @Column(nullable = false, length = 500)
    private String urlPerfilCliente;
    private Role roleCliente;

    //Relação do cliente com a entidade curtida
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL) //cascade reflete o que acontecer com o pai acontece com o filho
    private List<CurtidaModel>curtidas;

    // Relação da classe cliente com PreferenciaNotificacao
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private PreferenciaNotificacao preferenciaNotificacao;
}
