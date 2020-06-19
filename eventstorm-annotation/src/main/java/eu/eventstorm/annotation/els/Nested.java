package eu.eventstorm.annotation.els;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import eu.eventstorm.annotation.CqrsQueryProperty;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/date.html
 * 
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({ METHOD })
@Retention(RUNTIME)
@CqrsQueryProperty
public @interface Nested {

}