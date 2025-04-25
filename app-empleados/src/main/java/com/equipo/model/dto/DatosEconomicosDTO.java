package com.equipo.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Data
public class DatosEconomicosDTO {

    @NotBlank(message = "El número de cuenta IBAN es obligatorio.")
    @Pattern(regexp = "^[A-Z]{2}\\d{22}$", message = "El IBAN debe tener el formato correcto (ej. ES6600190020961234567890).")
    private String numeroCuenta;

    @NotBlank(message = "El tipo de contrato es obligatorio.")
    private String tipoContrato;

    @NotBlank(message = "La categoría profesional es obligatoria.")
    private String categoriaProfesional;

    @NotNull(message = "El salario base es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El salario base debe ser mayor que 0.")
    private BigDecimal salarioBaseMensual;

    @NotNull(message = "El complemento específico es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El complemento no puede ser negativo.")
    private BigDecimal complementoMensual;

    @NotBlank(message = "El devengo de la paga extra es obligatorio.")
    private String devengoPagaExtra;

    @NotBlank(message = "La fecha de incorporación es obligatoria.")
    private String fechaIncorporacion; // Usaremos String para manejar formato manualmente (ej. dd/MM/yyyy)
}
