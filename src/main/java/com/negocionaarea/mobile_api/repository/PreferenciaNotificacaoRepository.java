package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PreferenciaNotificacaoRepository extends JpaRepository<PreferenciaNotificacaoModel, UUID> {

}
