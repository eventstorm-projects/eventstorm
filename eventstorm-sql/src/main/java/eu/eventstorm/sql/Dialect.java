package eu.eventstorm.sql;

import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.expression.JsonPathDeepExpression;
import eu.eventstorm.sql.expression.JsonPathExpression;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Xml;
import eu.eventstorm.util.FastByteArrayInputStream;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Dialect {

	enum Name {
		H2, ORACLE, POSTGRES
	}

	void init();

	String nextVal(SqlSequence sequence);

	void wrap(Appendable appendable, SqlTable table, boolean alias);

	void wrap(Appendable appendable, SqlColumn targetColumn, boolean alias);

	// use for mapper.
	Json fromJdbcJson(ResultSet rs, int index) throws SQLException;

	Xml fromJdbcXml(ResultSet rs, int index) throws SQLException;

	Xml createXml(InputStream is);
	
	String limit(int limit);

	String range(int offset, int limit);
	
	void setPreparedStatement(PreparedStatement ps, int index, Json json) throws SQLException;

	void setPreparedStatement(PreparedStatement ps, int index, Xml xml) throws SQLException;

	void setPreparedStatement(PreparedStatement ps, int index, Blob blob) throws SQLException;
	
	void setPreparedStatement(PreparedStatement ps, int index, Clob clob) throws SQLException;

	void setPreparedStatement(PreparedStatement ps, int index, String uuid) throws SQLException;

	void setPreparedStatementJsonBinary(PreparedStatement ps, int index, String json) throws SQLException;

	void setPreparedStatementJsonBinaryNull(PreparedStatement ps, int index) throws SQLException;

	String functionJsonExists(String col, JsonPathExpression path);

	String functionJsonValue(String col, JsonPathDeepExpression path);

	String ilike(SqlColumn column, boolean alias);

	default int getBooleanType() {
		return Types.BOOLEAN;
	}

	default int getUuidType() { return Types.VARCHAR; }

	String toSql(Boolean value);

	String toSql(JsonPathExpression expression);

	String toSql(int type);
}
