package com.equipo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private String passwordFieldName;
    private String confirmPasswordFieldName;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        this.passwordFieldName = constraintAnnotation.password();
        this.confirmPasswordFieldName = constraintAnnotation.confirmPassword();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        Object password = beanWrapper.getPropertyValue(passwordFieldName);
        Object confirmPassword = beanWrapper.getPropertyValue(confirmPasswordFieldName);

        if (password == null && confirmPassword == null) {
            return true; // Ambos son nulos, se considera válido (o puedes cambiarlo a false si prefieres)
        }

        if (password == null || confirmPassword == null) {
            return false; // Uno es nulo y el otro no
        }

        return password.equals(confirmPassword);
    }
}