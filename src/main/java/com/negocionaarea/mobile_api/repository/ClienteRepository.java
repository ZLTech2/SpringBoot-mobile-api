package com.negocionaarea.mobile_api.repository;

import com.negocionaarea.mobile_api.model.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<ClienteModel, UUID> {
    Optional<ClienteModel> findByEmail(String email);

    @Query("""
            select c
            from ClienteModel c
            where c.dataNascimento is not null
              and month(c.dataNascimento) = :mes
              and day(c.dataNascimento) = :dia
            """)
    List<ClienteModel> findAniversariantesDoDia(@Param("mes") int mes, @Param("dia") int dia);
}
