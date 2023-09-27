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
public @interface Text {

    /**
     * Mapping field-level query time boosting. Accepts a floating point number,
     * defaults to 1.0.
     */
    float boost() default 1.0f;

    /**
     * Should the field be stored on disk in a column-stride fashion, so that it can
     * later be used for sorting, aggregations, or scripting? Accepts true (default)
     * or false.
     */
    boolean docValues() default true;

    /**
     * Should the field be searchable? Accepts true (default) and false.
     */
    boolean index() default true;

    /**
     * Accepts a date value in one of the configured format's as the field which is
     * substituted for any explicit null values. Defaults to null, which means the
     * field is treated as missing.
     */
    boolean nullValue() default true;

}