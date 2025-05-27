package com.equipo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvitacionColaboracionDTO {
    @NotBlank(message = "El email del destinatario es obligatorio.")
    @Email(message = "Debe proporcionar un email válido.")
    private String emailReceptor;
}