package eu.eventstorm.sql.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({METHOD})
@Retention(SOURCE)
public @interface ViewColumn {

    /**
     * Name of the column.
     */
    String value() default "";
    
    /**
     * Optional, this column is nullable. (only if type is an object and not a primitive).
     *
     * @return true if the column is nullable.
     */
    boolean nullable() default false;
    
}