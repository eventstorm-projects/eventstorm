package eu.eventstorm.sql.dialect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.common.BlobSqlJson;

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
        return "SELECT NEXTVAL('" + prefix(sequence) + "')";
    }

    @Override
    public String range(int offset, int limit) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }

	@Override
	public Json createSqlJson(Map<String, Object> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Json fromJdbcJson(ResultSet rs, int index) throws SQLException {
		return new BlobSqlJson(rs.getBytes(index));
	}

}
