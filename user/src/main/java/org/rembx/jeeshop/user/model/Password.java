package org.rembx.jeeshop.user.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotNull
@Size(min = 8, max = 50)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface Password {

    // ======================================
    // =             Attributes             =
    // ======================================

    String message() default "password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
