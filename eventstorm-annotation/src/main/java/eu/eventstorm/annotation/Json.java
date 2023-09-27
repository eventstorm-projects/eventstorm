package eu.eventstorm.annotation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Retention(SOURCE)
@CqrsQueryProperty
public @interface Json {

    boolean raw() default false;

}