package eu.eventstorm.sql.dialect;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlSequence;

final class H2Dialect extends AbstractDialect {

    public H2Dialect(Database database) {
        super(database);
    }

    @Override
    protected String aliasSeparator() {
        return " AS ";
    }

    @Override
    public String nextVal(SqlSequence sequence) {
        return "SELECT NEXTVAL('" + sequence.name() + "')";
    }

    @Override
    public String range(int offset, int limit) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }

}
