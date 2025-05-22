package com.equipo.repository;

import com.equipo.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository // Indica que esta interfaz es un repositorio de Spring Data JPA
public interface AdministradorRepository extends JpaRepository<Administrador, UUID> {

    // Método para buscar un administrador por su dirección de correo electrónico
    Optional<Administrador> findByEmail(String email);

    // Método para verificar si existe un administrador con un correo electrónico dado
    boolean existsByEmail(String email);
}