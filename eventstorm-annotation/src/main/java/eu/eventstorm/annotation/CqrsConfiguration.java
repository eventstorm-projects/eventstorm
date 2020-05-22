package eu.eventstorm.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CqrsConfiguration {

	OpenAPIDefinition openAPIDefinition();
	
	/**
	 * to generate SpringConfiguration, Streams, ...
	 */
	String basePackage();
	
}