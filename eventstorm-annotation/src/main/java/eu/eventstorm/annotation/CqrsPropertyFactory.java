package eu.eventstorm.annotation;

import eu.eventstorm.core.util.PropertyFactory;
import eu.eventstorm.core.util.PropertyFactoryType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({METHOD})
@Retention(SOURCE)
public @interface CqrsPropertyFactory {

    Class<? extends PropertyFactory> factory();

    PropertyFactoryType type() default PropertyFactoryType.STRING;

}