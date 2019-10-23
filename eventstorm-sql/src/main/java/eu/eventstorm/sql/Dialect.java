package eu.eventstorm.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

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
	
	Json createJson(Map<String,Object> value);
	
	Json createJson(byte[] value);
	
	Xml createXml(FastByteArrayInputStream fbais);
	
	String range(int offset, int limit);

}
