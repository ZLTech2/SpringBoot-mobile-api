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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, length = 100)
    private String nomeEmpresa;
    @Column(nullable = false, length = 14)
    private String cnpjEmpresa;
    @Column(nullable = false, length = 50)
    private String categoriaEmpresa;
    @Column(nullable = false, length = 100)
    private String bairroEmpresa;
    @Column(nullable = false, length = 50)
    private String contato;
    @Column(nullable = false, length = 50)
    private String emailEmpresa;
    @Column(nullable = false, length = 100)
    private String senhaEmpesa;
    @Column(nullable = false, length = 500)
    private String urlPerfilEmpresa;
    @Column(nullable = false, length = 500)
    private String urlCapaEmpresa;
    private Role roleEmpresa;

    // Relação da empresa com a entidade produto
    @OneToMany(mappedBy = "empresa")
    private List<ProdutoModel> posts;
}
