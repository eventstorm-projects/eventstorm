package eu.eventstorm.sql.apt.flyway;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.type.Json;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class FlywayDialectH2 implements FlywayDialect {

	static final FlywayDialect INSTANCE = new FlywayDialectH2();

	@Override
	public String toSqlType(String javaType, PrimaryKey column) {
		
		if (int.class.getName().equals(javaType) || Integer.class.getName().equals(javaType)) {
			return "INT";
		}

		if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
			return "BIGINT";
		}
		
		if (String.class.getName().equals(javaType)) {
			return "VARCHAR(" + column.length() + ")";
		}
		
		LoggerFactory.getInstance().getLogger(FlywayDialectH2.class).error("No sql type for java type (PrimaryKey) [" + javaType + "]");
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
			return "VARCHAR(" + column.length() + ")";
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
		
		if (Clob.class.getName().equals(javaType)) {
			return "CLOB";
		}
		
		LoggerFactory.getInstance().getLogger(FlywayDialectH2.class).error("No sql type for java type [" + javaType + "]");
		return null;
	}

	@Override
	public String wrap(String value) {
		return "\"" + value + "\"";
	}

	@Override
	public String autoIncrementType(String javaType) {

		if (int.class.getName().equals(javaType) || Integer.class.getName().equals(javaType)) {
			return "INT auto_increment";
		}

		if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
			return "BIGINT auto_increment";
		}

		LoggerFactory.getInstance().getLogger(FlywayDialectH2.class).error("No sql AUTO INCREMENT type for java type [" + javaType + "]");

		return null;
	}

}
