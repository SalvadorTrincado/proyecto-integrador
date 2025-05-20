package com.equipo.validation;

import com.equipo.dto.RegistroUsuarioDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// Validador que comprueba si los campos password y confirmPassword coinciden
public class PasswordCoincideValidacion implements ConstraintValidator<PasswordCoincideInterfas, RegistroUsuarioDTO> {

    @Override
    public boolean isValid(RegistroUsuarioDTO dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return false;
        }
        return dto.getPassword().equals(dto.getConfirmPassword());
    }
}
