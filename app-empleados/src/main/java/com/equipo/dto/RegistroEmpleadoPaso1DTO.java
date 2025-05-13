package com.equipo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class RegistroEmpleadoPaso1DTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 200, message = "Los apellidos deben tener entre 2 y 200 caracteres")
    private String apellidos;

    private MultipartFile fotografia; // Para la carga de archivos
    // Podrías añadir validaciones personalizadas para el tipo y tamaño del archivo si es necesario

    @NotBlank(message = "El género es obligatorio")
    private String generoSeleccionado;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 16, message = "La edad mínima debe ser 16 años")
    @Max(value = 99, message = "La edad máxima no debe exceder los 99 años") // Añadida validación de edad máxima (opcional)
    private Integer edad;

    @NotBlank(message = "El país de nacimiento es obligatorio")
    private String paisNacimiento;

    @Size(max = 500, message = "Los comentarios no pueden exceder los 500 caracteres")
    private String comentarios;
}