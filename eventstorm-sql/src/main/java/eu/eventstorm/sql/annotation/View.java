package eu.eventstorm.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.RetentionPolicy.SOURCE;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(SOURCE)
public @interface View {

    /**
     * Name for this sql table.
     */
    String value();

    /**
	 * enable support of page in repository
	 */
	boolean pageable() default false;
	
}