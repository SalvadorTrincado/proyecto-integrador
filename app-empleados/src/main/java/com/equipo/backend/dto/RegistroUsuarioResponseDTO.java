package com.equipo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioResponseDTO {

    private boolean exito;
    private String mensaje;
    private String email; // Opcional: podríamos incluir el correo electrónico registrado en la respuesta
}