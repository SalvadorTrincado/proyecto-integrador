package com.equipo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmpleadoRegistroDTO {
    private String nombre;
    private String apellidos;
    private String telefonoMovil;
    private String tipoDocumento;
    private String documento;
    private String departamento;
    private String categoriaProfesional;
    private String tipoContrato;
    private String numeroCuenta;
    private Double salarioBaseMensual;
    private Double complementoMensual;
    private String fechaIncorporacion;
}