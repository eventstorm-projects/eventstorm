package eu.eventstorm.sql.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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

    
    int length() default 0;
}