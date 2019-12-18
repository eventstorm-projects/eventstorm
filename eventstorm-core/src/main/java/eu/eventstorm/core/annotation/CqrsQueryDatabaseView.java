package eu.eventstorm.core.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import eu.eventstorm.sql.annotation.View;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
@CqrsQuery
public @interface CqrsQueryDatabaseView {

    View view();
}