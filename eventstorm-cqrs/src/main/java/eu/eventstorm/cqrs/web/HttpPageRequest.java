package eu.eventstorm.cqrs.web;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({PARAMETER})
@Retention(RUNTIME)
public @interface HttpPageRequest {

	String name();
	
}