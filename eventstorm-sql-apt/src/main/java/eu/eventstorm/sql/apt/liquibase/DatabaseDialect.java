package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;

public interface DatabaseDialect {

    String toSqlType(String string, Column column);

    String toSqlType(String string, PrimaryKey column);

    String autoIncrementType(String type);

    String wrap(String value);

}