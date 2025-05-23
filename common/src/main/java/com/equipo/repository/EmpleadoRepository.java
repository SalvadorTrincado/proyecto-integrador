package com.equipo.repository;


import com.equipo.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {
    @Query("""
        SELECT e FROM Empleado e
        WHERE (:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND (:departamento IS NULL OR e.departamento = :departamento)
        AND (:salarioMinimo IS NULL OR e.salarioBaseMensual >= :salarioMinimo)
        AND (:salarioMaximo IS NULL OR e.salarioBaseMensual <= :salarioMaximo)
    """)
    List<Empleado> buscarPorParametros(@Param("nombre") String nombre,
                                       @Param("departamento") String departamento,
                                       @Param("salarioMinimo") Double salarioMinimo,
                                       @Param("salarioMaximo") Double salarioMaximo);

    @Query("SELECT e FROM Empleado e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Empleado> buscarEmpleadosPorNombreConteniendo(@Param("nombre") String nombre);

    List<Empleado> findByNombre(String nombre);

    List<Empleado> findByApellidos(String apellidos);

    List<Empleado> findByDepartamento(String departamento);

    List<Empleado> findByEspecialidadesSeleccionadasContaining(String especialidad);
}
