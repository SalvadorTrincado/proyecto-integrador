package com.equipo.repository;

import com.equipo.entity.Colaboracion;
import com.equipo.entity.Empleado;
import com.equipo.entity.EstadoColaboracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ColaboracionRepository extends JpaRepository<Colaboracion, UUID> {

    List<Colaboracion> findByEmisorOrderByFechaInvitacionDesc(Empleado emisor);
    List<Colaboracion> findByReceptorOrderByFechaInvitacionDesc(Empleado receptor);
    List<Colaboracion> findByReceptorAndEstadoOrderByFechaInvitacionDesc(Empleado receptor, EstadoColaboracion estado);

    // Para evitar duplicados pendientes o ya aceptadas entre los mismos dos empleados
    Optional<Colaboracion> findByEmisorAndReceptorAndEstadoIn(Empleado emisor, Empleado receptor, List<EstadoColaboracion> estados);
}