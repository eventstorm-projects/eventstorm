package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.DerivedColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@FunctionalInterface
public interface AggregateFunction extends DerivedColumn {

    String build(Dialect dialect, boolean alias);
}
