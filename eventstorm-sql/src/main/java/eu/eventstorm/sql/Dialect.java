package eu.eventstorm.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.type.Json;

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
	
	Json createSqlJson(Map<String,Object> value);
	
	String range(int offset, int limit);

	/*
	 * SqlXml fromJdbcSqlXml(ResultSet rs, int index) throws SQLException;
	 * 
	 * SqlJson fromJdbcSqlJson(ResultSet rs, int index) throws SQLException;
	 * 
	 * SqlXml toJdbcSqlXml(FastByteArrayInputStream is);
	 * 
	 * SqlJson createSqlJson(Map<String,String> value);
	 * 
	 * SqlJson createSqlJson(String value);
	 */
}
