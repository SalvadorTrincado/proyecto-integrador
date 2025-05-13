package com.equipo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RegistroEmpleadoPaso3DTO {

    @NotBlank(message = "El departamento es obligatorio")
    @Size(min = 2, max = 100, message = "El departamento debe tener entre 2 y 100 caracteres")
    private String departamento;

    @NotEmpty(message = "Debe seleccionar al menos una especialidad")
    private List<String> especialidadesSeleccionadas;
}