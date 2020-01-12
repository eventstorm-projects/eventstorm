package eu.eventstorm.core.annotation.constrain;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Predicate;

import eu.eventstorm.core.annotation.Constraint;
import eu.eventstorm.util.tuple.Tuple;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
@Constraint
public @interface TupleValidator {

	String[] properties();
	
	Class<? extends Predicate<? extends Tuple>> predicate();
}
