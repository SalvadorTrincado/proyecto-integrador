package com.equipo.dto;

import com.equipo.validation.PasswordCoincideInterfas;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

// Anotaciones de Lombok para generar constructor, getters, setters y toString automáticamente
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@PasswordCoincideInterfas
public class RegistroUsuarioDTO {

    // Campo obligatorio y con formato de email válido
    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El email debe ser válido.")
    private String email;

    // Campo obligatorio, con longitud entre 8 y 12 y patrón de seguridad (mayúscula, minúscula, número y carácter especial)
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, max = 12, message = "La contraseña debe tener entre 8 y 12 caracteres.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "La contraseña debe contener una mayúscula, una minúscula, un número y un carácter especial."
    )
    private String password;

    // Campo obligatorio para confirmar que coincide con la contraseña
    @NotBlank(message = "La confirmación de la contraseña es obligatoria.")
    private String confirmPassword;

}