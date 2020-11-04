package eu.eventstorm.sql;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Xml;
import eu.eventstorm.util.FastByteArrayInputStream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Dialect {

	enum Name {
		H2, ORACLE, POSTGRES
	}

	String nextVal(SqlSequence sequence);

	void wrap(Appendable appendable, SqlTable table, boolean alias);

	void wrap(Appendable appendable, SqlColumn targetColumn, boolean alias);

	// use for mapper.
	Json fromJdbcJson(ResultSet rs, int index) throws SQLException;

	Xml fromJdbcXml(ResultSet rs, int index) throws SQLException;

	Xml createXml(FastByteArrayInputStream fbais);
	
	String limit(int limit);

	String range(int offset, int limit);
	
	void setPreparedStatement(PreparedStatement ps, int index, Json json) throws SQLException;
	
	void setPreparedStatement(PreparedStatement ps, int index, Blob blob) throws SQLException;
	
	void setPreparedStatement(PreparedStatement ps, int index, Clob clob) throws SQLException;

}
