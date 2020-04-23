package eu.eventsotrm.sql.apt.flyway;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface FlywayDialect {

	String toSqlType(String string, Column column);
	
	String toSqlType(String string, PrimaryKey column);

	String autoIncrementType(String type);
	
	String wrap(String value);

	

}
