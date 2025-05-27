package com.equipo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecuperacionClaveDTO {

    private String email; // Se puede pasar oculto o desde sesión

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 8, max = 12, message = "La contraseña debe tener entre 8 y 12 caracteres.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "La contraseña debe contener una mayúscula, una minúscula, un número y un carácter especial."
    )
    private String nuevaPassword;

    @NotBlank(message = "La confirmación de la nueva contraseña es obligatoria.")
    private String confirmarNuevaPassword;
}