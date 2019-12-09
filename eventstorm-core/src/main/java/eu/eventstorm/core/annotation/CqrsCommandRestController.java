package eu.eventstorm.core.annotation;

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

	String name();
    
    /**
     * default the package name of the command + '.rest'
     */
	String javaPackage() default "";
	
	String uri();
	
	HttpMethod method();
	
	boolean async() default true;
	
}