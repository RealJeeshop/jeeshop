package org.rembx.jeeshop.configuration;


import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface NamedConfiguration {
    @Nonbinding String value() default "";

    @Nonbinding String configurationFile() default "";
}