package eu.eventstorm.annotation.els;

import eu.eventstorm.annotation.CqrsQueryProperty;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/date.html
 *
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({METHOD})
@Retention(SOURCE)
@CqrsQueryProperty
public @interface Nested {

}