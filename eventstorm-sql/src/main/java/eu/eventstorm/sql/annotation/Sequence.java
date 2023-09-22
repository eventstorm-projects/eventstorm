package eu.eventstorm.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */

@Target({METHOD})
@Retention(SOURCE)
public @interface Sequence {

    /**
     * sequence name.
     */
    String value();


}