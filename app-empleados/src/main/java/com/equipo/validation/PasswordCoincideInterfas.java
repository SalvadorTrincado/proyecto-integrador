package com.equipo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

// Define una anotación personalizada para validar que las contraseñas coincidan
@Documented
@Constraint(validatedBy = PasswordCoincideValidacion.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordCoincideInterfas {
    String message() default "Las contraseñas deben coincidir";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
