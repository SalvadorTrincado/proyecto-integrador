package com.equipo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity // Indica que esta clase es una entidad de JPA (para la base de datos)
@Table(name = "administradores") // Especifica el nombre de la tabla en la base de datos
@Data // Genera automáticamente getters, setters, equals, hashCode y toString usando Lombok
public class Administrador {

    @Id // Marca este campo como la clave primaria
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column(name = "id", columnDefinition = "VARCHAR(36)") // Define la columna 'id' como VARCHAR de 36 caracteres
    private UUID id; // Identificador único del administrador (UUID)

    @Column(name = "email", nullable = false, unique = true) // Define la columna 'email' como no nula y única
    private String email; // Correo electrónico del administrador

    @Column(name = "password", nullable = false) // Define la columna 'password' como no nula
    private String password; // Contraseña del administrador (debe ser hasheada en la práctica)

    @Column(name = "numero_autenticacion", nullable = false, columnDefinition = "integer default 0") // Define la columna 'numero_autenticacion', no nula y con valor por defecto 0
    private Integer numeroAutenticacion = 0; // Contador del número de autenticaciones válidas

    @Column(name = "esta_habilitado", nullable = false, columnDefinition = "boolean default true") // Define la columna 'esta_habilitado', no nula y con valor por defecto true
    private Boolean estaHabilitado = true; // Indica si el administrador está habilitado
}