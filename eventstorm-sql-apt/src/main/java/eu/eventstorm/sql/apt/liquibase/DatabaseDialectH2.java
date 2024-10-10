package eu.eventstorm.sql.apt.liquibase;

import com.google.common.collect.ImmutableSet;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.ColumnFormat;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Xml;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseDialectH2 implements DatabaseDialect {

	// from https://www.h2database.com/html/advanced.html#keywords
	private static final ImmutableSet<String> KEYWORDS = ImmutableSet.<String>builder()
			.add("ALL")
			.add("AND")
			.add("ANY")
			.add("ARRAY")
			.add("AS")
			.add("ASYMMETRIC")
			.add("AUTHORIZATION")
			.add("BETWEEN")
			.add("BOTH")
			.add("CASE")
			.add("CAST")
			.add("CHECK")
			.add("CONSTRAINT")
			.add("CROSS")
			.add("CURRENT_CATALOG")
			.add("CURRENT_DATE")
			.add("CURRENT_PATH")
			.add("CURRENT_ROLE")
			.add("CURRENT_SCHEMA")
			.add("CURRENT_TIME")
			.add("CURRENT_TIMESTAMP")
			.add("CURRENT_USER")
			.add("DAY")
			.add("DEFAULT")
			.add("DISTINCT")
			.add("ELSE")
			.add("END")
			.add("EXCEPT")
			.add("EXISTS")
			.add("FALSE")
			.add("FETCH")
			.add("FOR")
			.add("FOREIGN")
			.add("FROM")
			.add("FULL")
			.add("GROUP")
			.add("GROUPS")
			.add("HAVING")
			.add("HOUR")
			.add("IF")
			.add("ILIKE")
			.add("IN")
			.add("INNER")
			.add("INTERSECT")
			.add("INTERVAL")
			.add("IS")
			.add("JOIN")
			.add("KEY")
			.add("LEADING")
			.add("LEFT")
			.add("LIKE")
			.add("LIMIT")
			.add("LOCALTIME")
			.add("LOCALTIMESTAMP")
			.add("MINUS")
			.add("MINUTE")
			.add("MONTH")
			.add("NATURAL")
			.add("NOT")
			.add("NULL")
			.add("OFFSET")
			.add("ON")
			.add("OR")
			.add("ORDER")
			.add("OVER")
			.add("PARTITION")
			.add("PRIMARY")
			.add("QUALIFY")
			.add("RANGE")
			.add("REGEXP")
			.add("RIGHT")
			.add("ROW")
			.add("ROWNUM")
			.add("ROWS")
			.add("SECOND")
			.add("SELECT")
			.add("SESSION_USER")
			.add("SET")
			.add("SOME")
			.add("SYMMETRIC")
			.add("SYSTEM_USER")
			.add("TABLE")
			.add("TO")
			.add("TOP")
			.add("")
			.add("TRAILING")
			.add("TRUE")
			.add("UESCAPE")
			.add("UNION")
			.add("UNIQUE")
			.add("UNKNOWN")
			.add("USER")
			.add("USING")
			.add("VALUE")
			.add("VALUES")
			.add("WHEN")
			.add("WHERE")
			.add("WINDOW")
			.add("WITH")
			.add("YEAR")
			.add("_ROWID_")
			.build();

	static final DatabaseDialect INSTANCE = new DatabaseDialectH2();

	static Logger logger;
	@Override
	public String toSqlType(String javaType, PrimaryKey column) {
		
		if (int.class.getName().equals(javaType) || Integer.class.getName().equals(javaType)) {
			return "INT";
		}

		if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
			return "BIGINT";
		}
		
		if (String.class.getName().equals(javaType)) {
			if (column.format() == ColumnFormat.UUID) {
				return "VARCHAR(36)";
			} else {
				return "VARCHAR(" + column.length() + ")";
			}
		}
		
		logger.error("No sql type for java type (PrimaryKey) [" + javaType + "]");
		return null;
	}
	
	@Override
	public String toSqlType(String javaType, Column column) {

		if (int.class.getName().equals(javaType) || Integer.class.getName().equals(javaType)) {
			return "INT";
		}

		if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
			return "BIGINT";
		}
		
		if (byte.class.getName().equals(javaType) || Byte.class.getName().equals(javaType)) {
			return "TINYINT";
		}

		if (boolean.class.getName().equals(javaType) || Boolean.class.getName().equals(javaType)) {
			return "BOOLEAN";
		}
		
		if (String.class.getName().equals(javaType)) {
			if (column.format() == ColumnFormat.UUID) {
				return "VARCHAR(36)";
			} else {
				return "VARCHAR(" + column.length() + ")";
			}
		}

		if (Timestamp.class.getName().equals(javaType)) {
			return "TIMESTAMP";
		}
		
		if (Date.class.getName().equals(javaType)) {
			return "DATE";
		}
		
		if (Time.class.getName().equals(javaType)) {
            return "TIME";
        }
		
		if (double.class.getName().equals(javaType) || Double.class.getName().equals(javaType)) {
			return "DOUBLE";
		}

		if (float.class.getName().equals(javaType) || Float.class.getName().equals(javaType)) {
			return "REAL";
		}
		
		if (Json.class.getName().equals(javaType)) {
			return "JSON";
		}

		if (Blob.class.getName().equals(javaType) || "byte[]".equals(javaType)) {
			return "BLOB";
		}
		
		if (Xml.class.getName().equals(javaType) || Clob.class.getName().equals(javaType)) {
			return "CLOB";
		}

		if (BigDecimal.class.getName().equals(javaType)) {
			return "DECFLOAT";
		}

		if ("long[]".equals(javaType) || "java.lang.Long[]".equals(javaType)) {
			return "BIGINT ARRAY";
		}
		
		logger.error("No sql type for java type [" + javaType + "]");
		return null;
	}

	@Override
	public String wrap(String value) {
		if (KEYWORDS.contains(value.toUpperCase())) {
			return "\"" + value + "\"";
		}
		return value;
	}

	@Override
	public String autoIncrementType(String javaType) {

		if (int.class.getName().equals(javaType) || Integer.class.getName().equals(javaType)) {
			return "INT auto_increment";
		}

		if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
			return "BIGINT auto_increment";
		}

		logger.error("No sql AUTO INCREMENT type for java type [" + javaType + "]");

		return null;
	}

}
