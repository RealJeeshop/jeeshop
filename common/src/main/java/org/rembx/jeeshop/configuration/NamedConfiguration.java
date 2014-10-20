package org.rembx.jeeshop.configuration;


import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface NamedConfiguration {
    @Nonbinding String value() default "";

    @Nonbinding String configurationFile() default "";
}