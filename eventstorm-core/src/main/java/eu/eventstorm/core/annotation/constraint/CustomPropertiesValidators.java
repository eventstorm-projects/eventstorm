package eu.eventstorm.core.annotation.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import eu.eventstorm.core.annotation.Constraint;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
@Constraint
public @interface CustomPropertiesValidators {

	CustomPropertiesValidator[] value();
	
}