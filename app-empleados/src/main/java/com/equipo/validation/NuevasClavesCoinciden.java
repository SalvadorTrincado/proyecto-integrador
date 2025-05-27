package com.equipo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NuevasClavesCoincidenValidacion.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NuevasClavesCoinciden {
    String message() default "Las nuevas contraseñas deben coincidir";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}