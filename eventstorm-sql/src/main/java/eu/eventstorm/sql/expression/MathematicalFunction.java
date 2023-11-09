package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.DerivedColumn;

public interface MathematicalFunction extends DerivedColumn {

    String build(Dialect dialect, boolean alias);

}
