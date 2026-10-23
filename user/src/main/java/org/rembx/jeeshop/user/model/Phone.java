package org.rembx.jeeshop.user.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Size(max = 15)
@Pattern(regexp = "\\+?[0-9\\s]+")
@ReportAsSingleViolation
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface Phone {

    // ======================================
    // =             Attributes             =
    // ======================================

    String message() default "phone";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
