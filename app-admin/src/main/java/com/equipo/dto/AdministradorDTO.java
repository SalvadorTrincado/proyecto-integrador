package com.equipo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.UUID;

@Data // Genera automáticamente getters, setters, equals, hashCode y toString usando Lombok
public class AdministradorDTO {

    private UUID id; // Identificador único del administrador

    @NotBlank(message = "{administrador.email.notblank}")
    @Email(message = "{administrador.email.formato}")
    private String email; // Correo electrónico del administrador

    @NotBlank(message = "{administrador.contraseña.notblank}")
    private String contraseña; // Contraseña del administrador

    @PositiveOrZero(message = "{administrador.numeroAutenticacion.positivo_cero}")
    private Integer numeroAutenticacion; // Número de autenticaciones válidas

    private Boolean estaHabilitado; // Indica si el administrador está habilitado
}