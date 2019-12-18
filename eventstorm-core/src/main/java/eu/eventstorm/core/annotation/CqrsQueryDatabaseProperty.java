package eu.eventstorm.core.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import eu.eventstorm.sql.annotation.ViewColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface CqrsQueryDatabaseProperty {

	ViewColumn column();
	
}