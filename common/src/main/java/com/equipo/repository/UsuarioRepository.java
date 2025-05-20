package com.equipo.repository;


import com.equipo.entity.Usuario;

import org.springframework.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Define el repositorio para la entidad 'Usuario', extendiendo JpaRepository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // Método para buscar un usuario por su 'id', asegurándose de que 'id' no sea null
    Optional<Usuario> findById(@NonNull UUID id);

    // Método para buscar un usuario por su 'email' (único), asegurándose de que 'email' no sea null
    Optional<Usuario> findByEmail(@NonNull String email);

    // Método para buscar un usuario por su 'password', asegurándose de que 'password' no sea null
    Optional<Usuario> findByPassword(@NonNull String password);
}
