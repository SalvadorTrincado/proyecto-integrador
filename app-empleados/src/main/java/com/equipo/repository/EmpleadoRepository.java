package com.equipo.repository;

import com.equipo.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {

    // Método para buscar un empleado por su 'id', asegurándose de que 'id' no sea null
    Optional<Empleado> findById(@NonNull UUID id);

    // **Nuevos métodos de búsqueda basados en campos de la entidad Empleado**

    // Buscar por nombre exacto
    List<Empleado> findByNombre(@NonNull String nombre);

    // Buscar por apellidos exactos
    List<Empleado> findByApellidos(@NonNull String apellidos);

    // Buscar empleados nacidos en una fecha específica
    List<Empleado> findByFechaNacimiento(@NonNull LocalDate fechaNacimiento);

    // Buscar empleados con una edad específica
    List<Empleado> findByEdad(@NonNull Integer edad);

    // Buscar empleados de un país de nacimiento específico
    List<Empleado> findByPaisNacimiento(@NonNull String paisNacimiento);

    // Buscar empleados por departamento exacto
    List<Empleado> findByDepartamento(@NonNull String departamento);

    // Buscar empleados que contengan una especialidad específica en su lista
    List<Empleado> findByEspecialidadesSeleccionadasContaining(@NonNull String especialidad);

    // Buscar empleados con un salario base mensual mayor o igual a un valor
    List<Empleado> findBySalarioBaseMensualGreaterThanEqual(@NonNull Double salarioBaseMensual);

    // **Ejemplo de consulta JPQL personalizada**
    @Query("SELECT e FROM Empleado e WHERE e.nombre LIKE %:nombre%")
    List<Empleado> buscarEmpleadosPorNombreConteniendo(@Param("nombre") @NonNull String nombre);

}