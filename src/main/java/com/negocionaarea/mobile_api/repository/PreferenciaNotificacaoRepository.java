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
    FROM preferencia_notificacao pn
    JOIN clientes c ON pn.cliente_id = c.id
    LEFT JOIN preferencia_categorias pc ON pc.preferencia_id = pn.id
    JOIN empresas e ON e.id = :empresaId
    WHERE 
        (
            LOWER(pc.categoria) = LOWER(:categoria)
            OR pn.receber_qualquer_promo = true
        )
    AND c.latitude IS NOT NULL
    AND c.longitude IS NOT NULL
    AND e.latitude IS NOT NULL
    AND e.longitude IS NOT NULL
    AND (
        6371 * acos(
            cos(radians(e.latitude)) *
            cos(radians(c.latitude)) *
            cos(radians(c.longitude) - radians(e.longitude)) +
            sin(radians(e.latitude)) *
            sin(radians(c.latitude))
        )
    ) <= pn.raio_maximo_km
""", nativeQuery = true)
    List<PreferenciaNotificacaoModel> buscarUsuariosParaNotificacao(
            @Param("categoria") String categoria,
            @Param("empresaId") UUID empresaId
    );
    Optional<PreferenciaNotificacaoModel> findByCliente_Id(UUID clienteId);

}
