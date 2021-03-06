package eu.eventstorm.annotation.els;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import eu.eventstorm.annotation.CqrsQueryProperty;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({ METHOD })
@Retention(RUNTIME)
@CqrsQueryProperty
public @interface Id {

}