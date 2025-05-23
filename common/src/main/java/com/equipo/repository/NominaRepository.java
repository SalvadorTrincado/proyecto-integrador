package com.equipo.repository;

import com.equipo.entity.Empleado;
import com.equipo.entity.Nomina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NominaRepository extends JpaRepository<Nomina, UUID> {

    // Para comprobar si existe una nómina para un empleado en un período que se solape [cite: 133]
    @Query("SELECT n FROM Nomina n WHERE n.empleado = :empleado AND " +
            "((n.fechaInicioPeriodo <= :fechaFin AND n.fechaFinPeriodo >= :fechaInicio) OR " +
            "(n.fechaInicioPeriodo >= :fechaInicio AND n.fechaInicioPeriodo <= :fechaFin) OR " +
            "(n.fechaFinPeriodo >= :fechaInicio AND n.fechaFinPeriodo <= :fechaFin))")
    List<Nomina> findByEmpleadoAndPeriodoSolapado(
            @Param("empleado") Empleado empleado,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    // Para obtener la última nómina cobrada (o la más reciente) de un empleado para validaciones de fecha [cite: 151]
    Optional<Nomina> findTopByEmpleadoOrderByFechaFinPeriodoDesc(Empleado empleado);

    // Consultas para administradores (filtrando por empleado y período, con paginación y ordenación) [cite: 136, 137, 138]
    // Ejemplo básico, se puede complejizar con JpaSpecificationExecutor si los criterios de ordenación son muy dinámicos
    Page<Nomina> findByEmpleadoAndFechaInicioPeriodoGreaterThanEqualAndFechaFinPeriodoLessThanEqual(
            Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable
    );

    Page<Nomina> findByEmpleado(Empleado empleado, Pageable pageable);

    Page<Nomina> findByFechaInicioPeriodoGreaterThanEqualAndFechaFinPeriodoLessThanEqual(
            LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable
    );

    // Consultas para empleados (sus propias nóminas, filtrando por período, con paginación y ordenación) [cite: 139, 140, 141]
    Page<Nomina> findByEmpleadoAndFechaFinPeriodoLessThanAndFechaInicioPeriodoGreaterThanEqualAndFechaFinPeriodoLessThanEqual(
            Empleado empleado, LocalDate fechaActual, LocalDate fechaInicioFiltro, LocalDate fechaFinFiltro, Pageable pageable
    );

    Page<Nomina> findByEmpleadoAndFechaFinPeriodoLessThan( // Nóminas ya cobradas
                                                           Empleado empleado, LocalDate fechaActual, Pageable pageable
    );

    // Para buscar una nómina de un empleado en un mes y año específico (útil para modificaciones/bajas)
    Optional<Nomina> findByEmpleadoAndFechaInicioPeriodo(Empleado empleado, LocalDate fechaInicioPeriodo);
}