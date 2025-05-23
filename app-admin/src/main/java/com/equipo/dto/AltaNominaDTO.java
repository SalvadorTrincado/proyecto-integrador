package com.equipo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AltaNominaDTO {

    @NotNull(message = "El ID del empleado es obligatorio.")
    private String empleadoId; // UUID del empleado como String

    @NotNull(message = "La fecha de inicio del período es obligatoria.")
    private LocalDate fechaInicioPeriodo;

    @NotNull(message = "La fecha de fin del período es obligatoria.")
    private LocalDate fechaFinPeriodo;

    // Campos para el snapshot de la empresa (pueden venir del frontend o ser recuperados en el backend)
    @NotBlank(message = "El nombre de la empresa es obligatorio.")
    private String nombreEmpresa;

    @NotBlank(message = "El CIF de la empresa es obligatorio.")
    private String cifEmpresa;

    private String direccionEmpresa;

    // Los datos del empleado (nombre completo, identificacion, etc.) se recuperarán en el backend usando el empleadoId.

    @Valid // Para que se validen las LineaNominaDto anidadas
    @NotEmpty(message = "La nómina debe tener al menos una línea.")
    @Size(min = 1, message = "La nómina debe tener al menos una línea.")
    private List<LineaNominaDTO> lineasNomina;

    // Los totales (devengos, deducciones, neto) se calcularán en el backend.
}