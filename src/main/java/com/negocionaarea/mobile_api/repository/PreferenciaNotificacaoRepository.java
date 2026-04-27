package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PreferenciaNotificacaoRepository extends JpaRepository<PreferenciaNotificacaoModel, UUID> {
    @Query(value = """
    SELECT DISTINCT pn.*
    FROM PREFERENCIA_NOTIFICACAO pn
    JOIN CLIENTES c ON pn.CLIENTE_ID = c.ID
    LEFT JOIN PREFERENCIA_CATEGORIAS pc ON pc.PREFERENCIA_ID = pn.ID
    JOIN EMPRESAS e ON e.ID = :empresaId
    WHERE 
        (
            pc.CATEGORIA = :categoria
            OR pn.RECEBER_QUALQUER_PROMO = true
        )
    AND c.LATITUDE IS NOT NULL
    AND c.LONGITUDE IS NOT NULL
    AND e.LATITUDE IS NOT NULL
    AND e.LONGITUDE IS NOT NULL
    AND (
        6371 * acos(
            cos(radians(e.LATITUDE)) *
            cos(radians(c.LATITUDE)) *
            cos(radians(c.LONGITUDE) - radians(e.LONGITUDE)) +
            sin(radians(e.LATITUDE)) *
            sin(radians(c.LATITUDE))
        )
    ) <= pn.RAIO_MAXIMO_KM
""", nativeQuery = true)
    List<PreferenciaNotificacaoModel> buscarUsuariosParaNotificacao(
            @Param("categoria") String categoria,
            @Param("empresaId") UUID empresaId
    );
    Optional<PreferenciaNotificacaoModel> findByCliente_Id(UUID clienteId);

}
