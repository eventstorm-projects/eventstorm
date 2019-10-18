package eu.eventsotrm.sql.apt;


import java.io.IOException;
import java.io.Writer;

import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventsotrm.sql.apt.model.PojoPropertyDescriptor;
import eu.eventstorm.sql.annotation.AutoIncrement;
import eu.eventstorm.sql.type.Json;

public final class Helper {


    public static void writeNewLine(Writer writer) throws IOException {
        writer.write(System.getProperty("line.separator"));
    }

    public static void writeNewLine(StringBuilder builder) {
        builder.append(System.getProperty("line.separator"));
    }

    public static void writePackage(Writer writer, String pack) throws IOException {
        writer.write("package ");
        writer.write(pack);
        writer.write(';');
        writeNewLine(writer);
    }

    public static void writeGenerated(Writer writer, String clazz) throws IOException {
        writeNewLine(writer);
        writer.write("import javax.annotation.Generated;");
        writeNewLine(writer);
        writeNewLine(writer);

        writer.write("@Generated(\"" + clazz + "\")");
        writeNewLine(writer);
    }

    public static String propertyName(String name) {
        StringBuilder builder = new StringBuilder(name.length());
        builder.append(Character.toLowerCase(name.charAt(0)));
        builder.append(name.substring(1));
        return builder.toString();
    }



    public static String toUpperCase(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toUpperCase(name.charAt(0)));
        for (int i = 1 ; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append('_');
            }
            builder.append(Character.toUpperCase(c));
        }
        return builder.toString();
    }

    public static String preparedStatementSetter(String type) {

        if ("int".equals(type) || "java.lang.Integer".equals(type)) {
            return "setInt";
        }

        if ("long".equals(type) || "java.lang.Long".equals(type)) {
            return "setLong";
        }

        if ("short".equals(type) || "java.lang.Short".equals(type)) {
            return "setShort";
        }

        if ("byte".equals(type) || "java.lang.Byte".equals(type)) {
            return "setByte";
        }

        if ("float".equals(type) || "java.lang.Float".equals(type)) {
            return "setFloat";
        }

        if ("double".equals(type) || "java.lang.Double".equals(type)) {
            return "setDouble";
        }

        if ("boolean".equals(type) || "java.lang.Boolean".equals(type)) {
            return "setBoolean";
        }

        if ("java.lang.String".equals(type)) {
            return "setString";
        }

        if ("java.sql.Date".equals(type)) {
            return "setDate";
        }

        if ("java.sql.Timestamp".equals(type)) {
            return "setTimestamp";
        }

        if (Json.class.getName().equals(type)) {
			return "setObject";
		}
        
        throw new UnsupportedOperationException("Helper.preparedStatementSetter -> type not supported -> [" + type + "]");
    }

    public static String preparedStatementGetter(String type) {

        if ("int".equals(type) || "java.lang.Integer".equals(type)) {
            return "getInt";
        }

        if ("long".equals(type) || "java.lang.Long".equals(type)) {
            return "getLong";
        }

        if ("short".equals(type) || "java.lang.Short".equals(type)) {
            return "getShort";
        }

        if ("byte".equals(type) || "java.lang.Byte".equals(type)) {
            return "getByte";
        }

        if ("float".equals(type) || "java.lang.Float".equals(type)) {
            return "getFloat";
        }

        if ("double".equals(type) || "java.lang.Double".equals(type)) {
            return "getDouble";
        }

        if ("boolean".equals(type) || "java.lang.Boolean".equals(type)) {
            return "getBoolean";
        }

        if ("java.lang.String".equals(type)) {
            return "getString";
        }

        if ("java.sql.Date".equals(type)) {
            return "getDate";
        }

        if ("java.sql.Timestamp".equals(type)) {
            return "getTimestamp";
        }

        if (Json.class.getName().equals(type)) {
			return "getObject";
		}
        throw new UnsupportedOperationException("Helper.preparedStatementGetter -> type not supported -> [" + type + "]");
    }

    public static boolean isPrimitiveType(String type) {
        if ("int".equals(type) || "short".equals(type) || "long".equals(type) || "byte".equals(type) ||
                "float".equals(type) || "double".equals(type) || "boolean".equals(type)) {
            return true;
        }
        return false;
    }

    public static String nullableType(String type) {

        if ( "java.lang.Integer".equals(type)) {
            return "java.sql.Types.INTEGER";
        }

        if ("java.lang.Long".equals(type)) {
            return "java.sql.Types.BIGINT";
        }

        if ( "java.lang.String".equals(type)) {
            return "java.sql.Types.VARCHAR";
        }

        if ("short".equals(type) || "java.lang.Short".equals(type)) {
            return "java.sql.Types.SMALLINT";
        }

        if ("byte".equals(type) || "java.lang.Byte".equals(type)) {
            return"java.sql.Types.TINYINT";
        }

        if ("java.sql.Timestamp".equals(type)) {
            return"java.sql.Types.TIMESTAMP";
        }

        throw new UnsupportedOperationException("Helper.nullableType -> type not supported -> [" + type + "]");

    }

    public static boolean hasAutoIncrementPK(PojoDescriptor desc) {
        for (PojoPropertyDescriptor ppd : desc.ids()) {
            if (ppd.getter().getAnnotation(AutoIncrement.class) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isArray(String type) {
        // not for byte[]
        return "long[]".equals(type) || "java.lang.Long[]".equals(type) ||
               "int[]".equals(type) || "java.lang.Integer[]".equals(type) ||
               "short[]".equals(type) || "java.lang.Short[]".equals(type);
    }


    public static String isDialectType(String type) {

        if ("io.m3.sql.jdbc.SqlXml".equals(type)) {
            return "fromJdbcSqlXml";
        }

        if (Json.class.getName().equals(type)) {
            return "fromJdbcJson";
        }

        return null;
    }
    
    public static boolean isFushable(String type) {

        if (Json.class.getName().equals(type)) {
            return true;
        }

        return false;
    }
}