package com.equipo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// Define la clase como una entidad de JPA
@Entity
@Table(name = "USUARIOS")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Usuario {

    // Define el campo 'id' como la clave primaria
    // El valor de 'id' se genera automáticamente
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id;

    // Especifica que el campo 'email' no puede ser nulo y debe ser único
    @Column(nullable = false, unique = true)
    private String email;

    // Especifica que el campo 'password' no puede ser nulo
    @Column(nullable = false)
    private String password;

    // Nuevos campos para el bloqueo de cuenta
    @Column(name = "intentos_fallidos", columnDefinition = "integer default 0")
    private int intentosFallidos = 0;

    @Column(name = "cuenta_bloqueada", columnDefinition = "boolean default false")
    private boolean cuentaBloqueada = false;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo; // Para saber cuándo se bloqueó y, opcionalmente, cuándo desbloquear

    // Nuevo campo para el contador de conexiones válidas del usuario (6d)
    @Column(name = "contador_conexiones_validas", columnDefinition = "integer default 0")
    private Integer contadorConexionesValidas = 0;
}