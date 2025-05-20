package com.equipo.dto.registroPasos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class DatosEconomicosDTO {

    @NotBlank(message = "{datosEconomicos.numeroCuenta.obligatorio}")
    @Pattern(regexp = "[A-Z]{2}[0-9]{2}[0-9A-Z]{1,30}", message = "{datosEconomicos.numeroCuenta.formato}")
    private String numeroCuenta;

    @NotBlank(message = "{datosEconomicos.tipoContrato.obligatorio}")
    private String tipoContrato;

    @NotBlank(message = "{datosEconomicos.categoriaProfesional.obligatoria}")
    private String categoriaProfesional;

    @NotNull(message = "{datosEconomicos.salarioBase.obligatorio}")
    @Positive(message = "{datosEconomicos.salarioBase.positivo}")
    private Double salarioBase;

    @NotNull(message = "{datosEconomicos.complemento.obligatorio}")
    private Double complemento;

    @NotBlank(message = "{datosEconomicos.devengo.obligatorio}")
    private String devengo;

    @NotNull(message = "{datosEconomicos.fechaIncorporacion.obligatoria}")
    private LocalDate fechaIncorporacion;
}
