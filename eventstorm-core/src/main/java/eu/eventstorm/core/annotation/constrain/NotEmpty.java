package eu.eventstorm.core.annotation.constrain;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import eu.eventstorm.core.annotation.Constraint;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({METHOD})
@Retention(RUNTIME)
@Constraint
public @interface NotEmpty {

}
