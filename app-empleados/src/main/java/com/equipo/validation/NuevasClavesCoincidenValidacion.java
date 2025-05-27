package com.equipo.validation;

import com.equipo.dto.RecuperacionClaveDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NuevasClavesCoincidenValidacion implements ConstraintValidator<NuevasClavesCoinciden, RecuperacionClaveDTO> {

    @Override
    public boolean isValid(RecuperacionClaveDTO dto, ConstraintValidatorContext context) {
        if (dto.getNuevaPassword() == null || dto.getConfirmarNuevaPassword() == null) {
            return false; // O true si se permite que sean nulos inicialmente y otra validación se encarga
        }
        boolean sonIguales = dto.getNuevaPassword().equals(dto.getConfirmarNuevaPassword());
        if (!sonIguales) {
            // Personalizar el mensaje para el campo 'confirmarNuevaPassword'
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmarNuevaPassword") // Asocia el error a este campo
                    .addConstraintViolation();
        }
        return sonIguales;
    }
}