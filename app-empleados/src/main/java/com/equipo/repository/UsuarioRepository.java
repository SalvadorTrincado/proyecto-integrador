package com.equipo.repository;

import com.equipo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;


import java.time.LocalDateTime;
import java.util.List;
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

    // **Nuevo método para buscar usuarios bloqueados**
    List<Usuario> findByCuentaBloqueadaTrue();

    // **Nuevo método para buscar usuarios no bloqueados**
    List<Usuario> findByCuentaBloqueadaFalse();

    // **Nuevo método para buscar usuarios bloqueados cuyo tiempo de desbloqueo ha expirado**
    @Query("SELECT u FROM Usuario u WHERE u.cuentaBloqueada = true AND u.tiempoDesbloqueo <= :ahora")
    List<Usuario> findCuentaBloqueadaConTiempoDesbloqueoExpirado(@Param("ahora") LocalDateTime ahora);

}
