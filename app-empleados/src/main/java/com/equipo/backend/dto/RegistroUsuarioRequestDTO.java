package com.equipo.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroUsuarioRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 12, message = "La contraseña debe tener entre 8 y 12 caracteres")
    // Puedes añadir una expresión regular más compleja si lo deseas
    // @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$",
    //          message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un signo de puntuación")
    private String clave;

    @NotBlank(message = "La confirmación de la contraseña es obligatoria")
    private String confirmarClave;

    // Podemos añadir una validación a nivel de clase para verificar que las contraseñas coinciden
    public boolean isClavesCoincidentes() {
        return this.clave != null && this.clave.equals(this.confirmarClave);
    }
}