package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@FunctionalInterface
public interface Expression {

    String build(Dialect dialect, boolean alias);

    default int countParameter() {
        return 1;
    }

}
