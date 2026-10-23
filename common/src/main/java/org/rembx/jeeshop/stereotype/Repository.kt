package org.rembx.jeeshop.stereotype

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Stereotype

@Stereotype
@ApplicationScoped
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Repository
