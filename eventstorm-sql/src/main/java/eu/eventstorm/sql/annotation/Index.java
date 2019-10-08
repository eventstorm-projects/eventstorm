package eu.eventstorm.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */

@Target({})
@Retention(RUNTIME)
public @interface Index {

    String columns();

    String name();

    boolean unique() default true;

}