package eu.eventstorm.annotation;

import eu.eventstorm.core.client.HttpHeadersConsumer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({PARAMETER})
@Retention(SOURCE)
@CqrsQuery
public @interface Headers {

    Class<? extends HttpHeadersConsumer> value();

}