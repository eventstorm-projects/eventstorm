package eu.eventstorm.sql;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;

/**
 * Created by jmilitello on 26/03/2017.
 */
public class TestDialect implements Dialect {
    @Override
    public String nextVal(SqlSequence sequence) {
        return null;
    }

    @Override
    public void wrap(Appendable appendable, SqlTable table, boolean alias) {

    }

    @Override
    public void wrap(Appendable appendable, SqlColumn targetColumn, boolean alias) {

    }

    @Override
    public String range(int offset, int limit) {
        return null;
    }
}
