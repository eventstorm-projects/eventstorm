package eu.eventstorm.annotation.constraint;

import eu.eventstorm.annotation.Constraint;
import eu.eventstorm.core.validation.PropertyValidator;
import eu.eventstorm.util.tuple.Tuple;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(SOURCE)
@Constraint
public @interface CustomPropertiesValidator {

    String name();

    String[] properties();

    Class<? extends PropertyValidator<? extends Tuple>> validateBy();

}