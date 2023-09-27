package eu.eventstorm.annotation.constraint;

import eu.eventstorm.annotation.Constraint;
import eu.eventstorm.core.validation.PropertyValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({METHOD})
@Retention(SOURCE)
@Constraint
public @interface CustomPropertyValidator {

    Class<? extends PropertyValidator<?>> validateBy();

    InstantiatorType instantiator() default InstantiatorType.STATIC;

}