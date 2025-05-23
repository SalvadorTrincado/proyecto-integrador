package com.equipo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaNominaDTO {

    private String empleadoId; // UUID del empleado para filtrar

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    // Campos para paginación y ordenación que se manejarán con Pageable
    // private int pagina;
    // private int tamanoPagina;
    // private String ordenarPor; // ej. "fechaInicioPeriodo,asc" o "empleado.nombre,desc"
    // private String direccionOrden;
}