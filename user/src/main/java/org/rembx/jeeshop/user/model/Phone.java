package org.rembx.jeeshop.user.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotNull
@Size(min = 1, max = 15)
@Constraint(validatedBy = {})
@Pattern(regexp = "\\+*[0-9]+")
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
