package com.equipo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnviarMensajeDTO {
    @NotBlank(message = "El mensaje no puede estar vacío.")
    @Size(max = 1000, message = "El mensaje no puede exceder los 1000 caracteres.")
    private String textoMensaje;
}