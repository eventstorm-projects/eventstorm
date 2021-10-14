package eu.eventstorm.annotation;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
public @interface SagaRestController {

    /**
     * Name of the RestController (Class name of the restController)
     */
	String name();

	String uri();
	
	/**
	 * Http method for this method, default is POST
	 */
	HttpMethod method() default HttpMethod.POST;

	/**
	 * Return type for this method; if default return type (Void.class) => return CloudEvent
	 */
	Class<?> returnType() default Void.class;

	/**
	 * response produce media type
	 */
	String produces() default "application/cloudevents+json";

}