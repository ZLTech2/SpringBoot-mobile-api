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
@Table(name = "empresa")
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
<<<<<<< HEAD
    @Column(nullable = false, length = 14)
    private String cnpj;
=======

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

>>>>>>> master
    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false, length = 50)
    private String telefone;
<<<<<<< HEAD
    @Column(nullable = false, length = 50)
    private String email;
    @Column(length = 100)
    private String descricao;
    @Column(nullable = false, length = 100)
    private String senha;

=======
    
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(length = 100)
    private String descricao;

    @Column(nullable = false, length = 100)
    private String senha; // 
>>>>>>> master

    private Role roleEmpresa;
    @OneToMany
    private List<ProdutoModel> posts;
<<<<<<< HEAD
=======

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private EnderecoModel endereco;
>>>>>>> master
}
