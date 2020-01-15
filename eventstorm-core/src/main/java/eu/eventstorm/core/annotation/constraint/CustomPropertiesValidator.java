package eu.eventstorm.core.annotation.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import eu.eventstorm.core.annotation.Constraint;
import eu.eventstorm.core.validation.PropertyValidator;
import eu.eventstorm.util.tuple.Tuple;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
@Constraint
public @interface CustomPropertiesValidator {
	
	String name();

	String[] properties();
	
	Class<? extends PropertyValidator<? extends Tuple>> validateBy();
	
}