package com.equipo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}