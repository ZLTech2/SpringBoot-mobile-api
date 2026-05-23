package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.CurtidaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurtidaRepository extends JpaRepository<CurtidaModel, UUID> {
    boolean existsByClienteIdAndProdutoIdProduto(UUID clienteId, UUID idProduto);
    List<CurtidaModel> findAllByClienteIdOrderByDataHoraDesc(UUID clienteId);
    Optional<CurtidaModel> findFirstByClienteIdAndProdutoIdProduto(UUID clienteId, UUID idProduto);


    /** total de curtidas de todos os produtos */
    @Query("""
        SELECT COUNT(c) FROM CurtidaModel c
        WHERE c.produto.empresa.id = :empresaId
""")
    Long countTotalByEmpresaId(@Param("empresaId") UUID empresaId);

    /** curtida pelo intervalo de datas */
    @Query("""
        SELECT COUNT(c) FROM CurtidaModel c
        WHERE c.produto.empresa.id = :empresaId
        AND c.dataHora BETWEEN :inicio AND :fim
""")
    Long countByEmpresaIdAndPeriodo(@Param("empresaId") UUID empresaId, @Param("inicio")LocalDateTime inicio, @Param("fim")LocalDateTime fim);

    @Query("""
        SELECT CAST(c.dataHora AS date) as dia, COUNT(c) as total
        FROM CurtidaModel c
        WHERE c.produto.empresa.id = :empresaId
          AND c.dataHora BETWEEN :inicio AND :fim
        GROUP BY CAST(c.dataHora AS date)
        ORDER BY CAST(c.dataHora AS date)
    """)
    List<Object[]> countPorDia(
            @Param("empresaId") UUID empresaId,
            @Param("inicio")    LocalDateTime inicio,
            @Param("fim")       LocalDateTime fim
    );

    /** Curtidas agrupadas por hora do dia (0–23) para uma empresa em um período */
    @Query("""
        SELECT FUNCTION('HOUR', c.dataHora) as hora, COUNT(c) as total
        FROM CurtidaModel c
        WHERE c.produto.empresa.id = :empresaId
          AND c.dataHora BETWEEN :inicio AND :fim
        GROUP BY FUNCTION('HOUR', c.dataHora)
        ORDER BY FUNCTION('HOUR', c.dataHora)
    """)
    List<Object[]> countPorHora(
            @Param("empresaId") UUID empresaId,
            @Param("inicio")    LocalDateTime inicio,
            @Param("fim")       LocalDateTime fim
    );

    /** Curtidas agrupadas por dia da semana (1=Dom … 7=Sáb) para uma empresa */
    @Query("""
        SELECT FUNCTION('DAYOFWEEK', c.dataHora) as dow, COUNT(c) as total
        FROM CurtidaModel c
        WHERE c.produto.empresa.id = :empresaId
          AND c.dataHora BETWEEN :inicio AND :fim
        GROUP BY FUNCTION('DAYOFWEEK', c.dataHora)
        ORDER BY FUNCTION('DAYOFWEEK', c.dataHora)
    """)
    List<Object[]> countPorDiaSemana(
            @Param("empresaId") UUID empresaId,
            @Param("inicio")    LocalDateTime inicio,
            @Param("fim")       LocalDateTime fim
    );

    /** Curtidas agrupadas pelo bairro do cliente para uma empresa em um período */
    @Query("""
        SELECT c.cliente.endereco.bairro as bairro, COUNT(c) as total
        FROM CurtidaModel c
        WHERE c.produto.empresa.id = :empresaId
          AND c.dataHora BETWEEN :inicio AND :fim
          AND c.cliente.endereco.bairro IS NOT NULL
        GROUP BY c.cliente.endereco.bairro
        ORDER BY COUNT(c) DESC
    """)
    List<Object[]> countPorBairro(
            @Param("empresaId") UUID empresaId,
            @Param("inicio")    LocalDateTime inicio,
            @Param("fim")       LocalDateTime fim
    );

    /** Produtos da empresa ordenados por número de curtidas no período */
    @Query("""
        SELECT c.produto.nome as nome, COUNT(c) as total, c.produto.imagem as imagem
        FROM CurtidaModel c
        WHERE c.produto.empresa.id = :empresaId
          AND c.dataHora BETWEEN :inicio AND :fim
        GROUP BY c.produto.idProduto, c.produto.nome, c.produto.imagem
        ORDER BY COUNT(c) DESC
    """)
    List<Object[]> topProdutosPorCurtidas(
            @Param("empresaId") UUID empresaId,
            @Param("inicio")    LocalDateTime inicio,
            @Param("fim")       LocalDateTime fim
    );

    /** média de curtdas por produto de uma empresa em um período*/
    @Query("""
    SELECT COUNT(c)
    FROM CurtidaModel c
    WHERE c.produto.empresa.id = :empresaId
      AND c.dataHora BETWEEN :inicio AND :fim
    GROUP BY c.produto.idProduto
""")
    List<Long> countCurtidasPorProduto(
            @Param("empresaId") UUID empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
