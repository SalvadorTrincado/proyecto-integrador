package com.equipo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
public class LoginPaso2DTO {

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}