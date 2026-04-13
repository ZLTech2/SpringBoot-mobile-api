package com.negocionaarea.mobile_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.negocionaarea.mobile_api.dto.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false, length = 50)
    private String telefone;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(length = 100)
    private String descricao;

    @Column(nullable = false, length = 100)
    private String senha; //

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private EnderecoModel endereco;

    // Relação da empresa com a entidade produto
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProdutoModel> posts;
}
