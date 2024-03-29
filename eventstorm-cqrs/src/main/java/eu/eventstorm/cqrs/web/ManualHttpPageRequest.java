package eu.eventstorm.cqrs.web;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({PARAMETER})
@Retention(RUNTIME)
public @interface ManualHttpPageRequest {

    Class<?> query();

}