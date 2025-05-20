package com.equipo.dto.registroPasos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DatosProfesionalesDTO {

    @NotBlank(message = "{datosProfesionales.departamento.obligatorio}")
    private String departamento;

    @Size(min = 2, message = "{datosProfesionales.especialidades.minimo}")
    private List<String> especialidadesSeleccionadas;

    private String observaciones;
}
