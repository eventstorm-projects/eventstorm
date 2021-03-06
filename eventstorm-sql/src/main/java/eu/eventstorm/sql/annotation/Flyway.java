package eu.eventstorm.sql.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({})
@Retention(RUNTIME)
public @interface Flyway {

	String version();

	String description();

}