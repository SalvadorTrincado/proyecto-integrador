package com.equipo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat; // Asegúrate de importar esto

import java.time.LocalDate;

@Data
public class RegistroEmpleadoPaso4DTO {

    @NotBlank(message = "{datosEconomicos.numeroCuenta.obligatorio}")
    @Pattern(regexp = "^[A-Z]{2}\\d{22}$", message = "{datosEconomicos.numeroCuenta.formato}")
    private String numeroCuenta;

    @NotBlank(message = "{datosEconomicos.tipoContrato.obligatorio}")
    private String tipoContrato;

    @NotBlank(message = "{datosEconomicos.categoriaProfesional.obligatoria}")
    private String categoriaProfesional;

    @NotNull(message = "{datosEconomicos.salarioBase.obligatorio}")
    @DecimalMin(value = "0.01", message = "{datosEconomicos.salarioBase.positivo}")
    private Double salarioBaseMensual;

    private Double complementoMensual;

    @NotBlank(message = "{datosEconomicos.devengo.obligatorio}")
    private String devengoPagaExtra;

    @NotNull(message = "{datosEconomicos.fechaIncorporacion.obligatoria}")
    @PastOrPresent(message = "La fecha de incorporación no puede ser en el futuro")
    @DateTimeFormat(pattern = "dd/MM/yyyy") // Especifica el formato esperado
    private LocalDate fechaIncorporacion;
}