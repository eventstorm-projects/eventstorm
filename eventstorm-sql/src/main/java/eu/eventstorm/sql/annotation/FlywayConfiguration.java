package eu.eventstorm.sql.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Retention(RUNTIME)
public @interface FlywayConfiguration {

	Database[] database();
	
	String[] packages();
	
}
