package org.rembx.jeeshop.configuration

import jakarta.enterprise.util.Nonbinding
import jakarta.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.ANNOTATION_CLASS,
)
annotation class NamedConfiguration(
    @get:Nonbinding val value: String = "",
    @get:Nonbinding val configurationFile: String = "",
)
