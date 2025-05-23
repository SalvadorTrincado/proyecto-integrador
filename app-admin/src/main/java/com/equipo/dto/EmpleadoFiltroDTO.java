package com.equipo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoFiltroDTO {
    private String nombre;
    private String departamento;
    private Double salarioMinimo;
    private Double salarioMaximo;
}
