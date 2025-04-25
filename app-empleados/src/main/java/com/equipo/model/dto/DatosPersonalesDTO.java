package com.equipo.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


@Data
public class DatosPersonalesDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidos;

    @NotNull
    private MultipartFile fotografia;

    @NotBlank
    private String generoSeleccionado;

    @NotBlank
    private String fechaNacimiento; // dd/MM/yyyy

    @NotNull
    private Integer edad;

    @NotBlank
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
