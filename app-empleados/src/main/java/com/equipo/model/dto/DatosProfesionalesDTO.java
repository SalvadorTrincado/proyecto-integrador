package com.equipo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Data
public class DatosProfesionalesDTO {

    @NotBlank(message = "El departamento es obligatorio.")
    private String departamento;

    @Size(min = 2, message = "Debes seleccionar al menos 2 especialidades.")
    private List<String> especialidadesSeleccionadas;
}
