package com.parser.core.util.annotation;

import com.parser.core.util.validator.UniqueNameWithRdtValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueNameWithRdtValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueNameWithRdt {

    String message() default "Role with the same name and NULL rdt already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
