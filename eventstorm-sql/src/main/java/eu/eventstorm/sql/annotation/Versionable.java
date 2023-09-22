package eu.eventstorm.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Target({TYPE})
@Retention(SOURCE)
public @interface Versionable {

    String version() default "1.0.0";

}