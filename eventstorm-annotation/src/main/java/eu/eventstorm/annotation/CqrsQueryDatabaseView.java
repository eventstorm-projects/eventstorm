package eu.eventstorm.annotation;

import eu.eventstorm.sql.annotation.View;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(SOURCE)
@CqrsQuery
public @interface CqrsQueryDatabaseView {

    View view();
}