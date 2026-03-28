package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PreferenciaNotificacaoRepository extends JpaRepository<PreferenciaNotificacaoModel, UUID> {
}
