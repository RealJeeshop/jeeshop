package org.rembx.jeeshop.user.domain

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.ReportAsSingleViolation
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.VALUE_PARAMETER,
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Size(min = 5)
@Pattern(
    regexp = """[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*""" +
        """@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?""",
)
@ReportAsSingleViolation
@Constraint(validatedBy = [])
annotation class Email(
    val message: String = "email",
    val groups: Array<kotlin.reflect.KClass<*>> = [],
    val payload: Array<kotlin.reflect.KClass<out Payload>> = [],
) {
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.FIELD,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.VALUE_PARAMETER,
    )
    @Retention(AnnotationRetention.RUNTIME)
    annotation class List(val value: Array<Email>)
}
