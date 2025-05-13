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
    private UUID id;

    // Especifica que el campo 'email' no puede ser nulo y debe ser único
    @Column(nullable = false, unique = true)
    private String email;

    // Especifica que el campo 'password' no puede ser nulo
    @Column(nullable = false)
    private String password;

    // Nuevo campo para almacenar el número de intentos fallidos (en español)
    @Column(name = "intentos_fallidos", nullable = false, columnDefinition = "integer default 0")
    private int intentosFallidos;

    // Nuevo campo para almacenar la última vez que hubo un intento fallido (en español)
    @Column(name = "ultimo_intento_fallido")
    private LocalDateTime ultimoIntentoFallido;

    // Nuevo campo para indicar si la cuenta está bloqueada (en español)
    @Column(name = "cuenta_bloqueada", nullable = false, columnDefinition = "boolean default false")
    private boolean cuentaBloqueada;

    // Nuevo campo para almacenar la fecha y hora en que la cuenta se desbloqueará (en español)
    @Column(name = "tiempo_desbloqueo")
    private LocalDateTime tiempoDesbloqueo;
}