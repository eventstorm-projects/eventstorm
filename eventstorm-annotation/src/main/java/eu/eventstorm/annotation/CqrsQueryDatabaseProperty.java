package eu.eventstorm.annotation;

import eu.eventstorm.sql.annotation.ViewColumn;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({METHOD})
@Retention(SOURCE)
@CqrsQueryProperty
public @interface CqrsQueryDatabaseProperty {

    ViewColumn column();

    Json json() default @Json;

}