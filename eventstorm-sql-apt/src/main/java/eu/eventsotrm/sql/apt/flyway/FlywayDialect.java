package eu.eventsotrm.sql.apt.flyway;

import eu.eventstorm.sql.annotation.Column;

public interface FlywayDialect {

	String toSqlType(String string, Column column);

	String autoIncrementType(String type);
	
	String wrap(String value);

	

}
