package com.equipo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistroEmpleadoPaso4DTO {

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Pattern(regexp = "^[A-Z]{2}\\d{22}$", message = "El IBAN debe tener el formato correcto (ej. ES6600190020961234567890).")
    private String numeroCuenta;

    @NotBlank(message = "El tipo de contrato es obligatorio")
    private String tipoContrato;

    @NotBlank(message = "La categoría profesional es obligatoria")
    private String categoriaProfesional;

    @NotNull(message = "El salario base mensual es obligatorio")
    @DecimalMin(value = "0.01", message = "El salario base mensual debe ser mayor que 0")
    private Double salarioBaseMensual;

    private Double complementoMensual; // No marcado como @NotBlank/@NotNull ya que podría ser opcional

    @NotBlank(message = "El devengo de paga extra es obligatorio")
    private String devengoPagaExtra;

    @NotNull(message = "La fecha de incorporación es obligatoria")
    @PastOrPresent(message = "La fecha de incorporación no puede ser en el futuro")
    private LocalDate fechaIncorporacion;
}