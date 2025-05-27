package com.equipo.repository;

import com.equipo.entity.Colaboracion;
import com.equipo.entity.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, UUID> {

    List<MensajeChat> findByColaboracionOrderByFechaHoraEnvioAsc(Colaboracion colaboracion);

    List<MensajeChat> findByColaboracionAndLeidoIsFalse(Colaboracion colaboracion);
}