package eu.eventstorm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface SagaCommand {

	SagaRestController controller();

    String name();

}