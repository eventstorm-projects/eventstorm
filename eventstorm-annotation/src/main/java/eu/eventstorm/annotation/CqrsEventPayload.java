package eu.eventstorm.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import eu.eventstorm.core.DomainModel;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CqrsEventPayload {

    Class<? extends DomainModel> domain();
    
    /**
     * if not set -> use the FCQN of the Interface.
     */
    String type() default "";
    
    /**
     * version of the type
     */
    byte version() default 1;
    
}