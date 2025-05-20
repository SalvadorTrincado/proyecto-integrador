package com.equipo.repository;


import com.equipo.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // Puedes añadir métodos personalizados si los necesitas
}
