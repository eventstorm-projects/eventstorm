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
public @interface Table {

    /**
     * Name for this sql table.
     */
    String value();

    /**
     * set this immutable or not. if false (mutable table) -> generate
     * {@link eu.eventstorm.sql.Repository} method ***forUpdate
     */
    boolean immutable() default false;

    /**
     * enable support of page in repository
     */
    boolean pageable() default false;

    /**
     * (Optional) Indexes for the table.
     */
    Index[] indexes() default {};

    Versionable versionable() default @Versionable;

}