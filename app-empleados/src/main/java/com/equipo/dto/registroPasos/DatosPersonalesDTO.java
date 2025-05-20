package com.equipo.dto.registroPasos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@ToString
public class DatosPersonalesDTO {

    @NotBlank(message = "{datosPersonales.nombre.obligatorio}")
    private String nombre;

    @NotBlank(message = "{datosPersonales.apellidos.obligatorio}")
    private String apellidos;

    @NotNull
    private MultipartFile fotografia;

    @NotBlank(message = "{datosPersonales.genero.obligatorio}")
    private String generoSeleccionado;

    @NotBlank(message = "{datosPersonales.fechaNacimiento.obligatoria}")
    private String fechaNacimiento;

    @NotNull
    private Integer edad;

    @NotBlank(message = "{datosPersonales.paisNacimiento.obligatorio}")
    private String paisNacimiento;

    private String comentarios;

    public boolean edadCorrecta() {
        try {
            LocalDate fecha = LocalDate.parse(fechaNacimiento, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            long edadCalculada = ChronoUnit.YEARS.between(fecha, LocalDate.now());
            return edad == (int) edadCalculada && edad >= 18;
        } catch (Exception e) {
            return false;
        }
    }
}
