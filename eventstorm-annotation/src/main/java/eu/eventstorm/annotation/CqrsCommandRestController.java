package eu.eventstorm.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CqrsCommandRestController {

    /**
     * Name of the RestController (Class name of the restController)
     */
	String name();
    
    /**
     * default the package name of the command + '.rest'
     */
	String javaPackage() default "";
	
	String uri();
	
	/**
	 * Http method for this method, default is POST
	 */
	HttpMethod method() default HttpMethod.POST;
	
	String eventLoop() default "event_store_scheduler";
	
	/**
	 * Return type for this method; if default return type (Void.class) => return CloudEvent
	 */
	Class<?> returnType() default Void.class;
	
}