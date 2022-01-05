package eu.eventstorm.annotation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Retention(RUNTIME)
@CqrsQueryProperty
public @interface Json {

    boolean raw() default false;

}