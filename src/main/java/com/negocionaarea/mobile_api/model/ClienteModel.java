package com.negocionaarea.mobile_api.model;

import com.negocionaarea.mobile_api.dto.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, length = 100)
    private String nome;
    @Column(nullable = false, length = 50)
    private String email;
    @Column(nullable = false, unique = true, length = 100)
    private String senha;
    @Column(nullable = true, length = 500)
    private String urlPerfil;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, length = 20)
    private String telefone;

    //Relação do cliente com a entidade curtida
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL) //cascade reflete o que acontecer com o pai acontece com o filho
    private List<CurtidaModel>curtidas;

    @Embedded
    private EnderecoModel endereco;

    @Embedded
    private LocalizacaoModel localizacao;
}
