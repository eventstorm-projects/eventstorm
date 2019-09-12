package eu.eventstorm.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import eu.eventstorm.sql.id.Identifier;
import eu.eventstorm.sql.id.NoIdentifierGenerator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface PrimaryKey {

    /**
     * Name of the column for this primary key.
     */
    String value();

    @SuppressWarnings("rawtypes")
	Class< ? extends Identifier> generator() default NoIdentifierGenerator.class;

}