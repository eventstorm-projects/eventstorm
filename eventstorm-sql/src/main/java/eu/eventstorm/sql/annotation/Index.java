package eu.eventstorm.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */

@Target({})
@Retention(SOURCE)
public @interface Index {

    String[] columns();

    String name();

    boolean unique() default true;

    String version() default "1.0.0";

}