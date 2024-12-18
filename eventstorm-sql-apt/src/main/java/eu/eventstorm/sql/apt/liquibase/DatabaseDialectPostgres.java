package eu.eventstorm.sql.apt.liquibase;

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
import java.sql.Timestamp;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseDialectPostgres implements DatabaseDialect {

    static final DatabaseDialect INSTANCE = new DatabaseDialectPostgres();

    static Logger logger;

    @Override
    public String toSqlType(String javaType, PrimaryKey column) {


        if (int.class.getName().equals(javaType) || Integer.class.getName().equals(javaType)) {
            return "INTEGER";
        }

        if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
            return "BIGINT";
        }

        if (String.class.getName().equals(javaType)) {
            if (column.format() == ColumnFormat.UUID) {
                return "UUID";
            } else if (column.format() == ColumnFormat.TEXT) {
                return "TEXT";
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
            return "INTEGER";
        }

        if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
            return "BIGINT";
        }

        if (byte.class.getName().equals(javaType) || Byte.class.getName().equals(javaType)) {
            return "SMALLINT";
        }

        if (boolean.class.getName().equals(javaType) || Boolean.class.getName().equals(javaType)) {
            return "BOOLEAN";
        }

        if (String.class.getName().equals(javaType)) {
            if (column.format() == ColumnFormat.UUID) {
                return "UUID";
            } else if (column.format() == ColumnFormat.TEXT) {
                return "TEXT";
            } else {
                return "VARCHAR(" + column.length() + ")";
            }
        }

        if (Timestamp.class.getName().equals(javaType)) {
            return "TIMESTAMP WITH TIME ZONE";
        }

        if (Date.class.getName().equals(javaType)) {
            return "DATE";
        }

        if (double.class.getName().equals(javaType) || Double.class.getName().equals(javaType)) {
            return "DECIMAL";
        }

        if (float.class.getName().equals(javaType) || Float.class.getName().equals(javaType)) {
            return "DECIMAL";
        }

        if (Json.class.getName().equals(javaType)) {
            return "JSONB";
        }

        if (Xml.class.getName().equals(javaType)) {
            return "XML";
        }

        if (Blob.class.getName().equals(javaType) || "byte[]".equals(javaType)) {
            return "BLOB";
        }

        if (Clob.class.getName().equals(javaType)) {
            return "CLOB";
        }

        if ("long[]".equals(javaType) || "java.lang.Long[]".equals(javaType)) {
            return "int8[]";
        }

        if ("int[]".equals(javaType) || "java.lang.Integer[]".equals(javaType)) {
            return "int4[]";
        }

        if (BigDecimal.class.getName().equals(javaType)) {
            return "NUMERIC";
        }

        logger.error("No sql type for java type [" + javaType + "]");
        return null;
    }

    @Override
    public String wrap(String value) {
        return value;
    }

    /**
     * https://chartio.com/resources/tutorials/how-to-define-an-auto-increment-primary-key-in-oracle/
     */
    @Override
    public String autoIncrementType(String javaType) {

        if (int.class.getName().equals(javaType) || Integer.class.getName().equals(javaType)) {
            return "SERIAL";
        }

        if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
            return "BIGSERIAL";
        }

        logger.error("No sql AUTO INCREMENT type for java type [" + javaType + "]");

        return null;
    }
}
