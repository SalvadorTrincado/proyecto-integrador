package com.equipo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NominaDetalleDTO {

    private String id; // UUID
    private String empleadoId;
    private String nombreCompletoEmpleado;
    private String identificacionEmpleado;
    private String puestoProfesionalEmpleado;
    private String departamentoEmpleado;
    private LocalDate fechaAltaEmpleado;

    private LocalDate fechaInicioPeriodo;
    private LocalDate fechaFinPeriodo;

    private String nombreEmpresa;
    private String cifEmpresa;
    private String direccionEmpresa;
    // Opcionales según PDF "Gestión de Nóminas"
    private String numeroSeguridadSocialEmpleado;
    private String direccionEmpleadoPersonal;


    private List<LineaNominaDTO> lineasNomina;

    private BigDecimal totalDevengos;
    private BigDecimal totalDeducciones;
    private BigDecimal netoAPercibir;

    // Campos opcionales para acumulados anuales
    private BigDecimal brutoAcumuladoAnual;
    private BigDecimal retencionesAcumuladasAnual;
    private BigDecimal percibidoAcumuladoAnual;
}