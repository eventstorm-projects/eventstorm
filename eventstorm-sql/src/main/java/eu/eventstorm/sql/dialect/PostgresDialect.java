package eu.eventstorm.sql.dialect;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.expression.JsonPathArrayExpression;
import eu.eventstorm.sql.expression.JsonPathDeepExpression;
import eu.eventstorm.sql.expression.JsonPathExpression;
import eu.eventstorm.sql.expression.JsonPathFieldsExpression;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Xml;
import eu.eventstorm.sql.type.postgres.JsonPGobject;
import eu.eventstorm.util.FastByteArrayInputStream;
import eu.eventstorm.util.Strings;
import org.postgresql.util.PGobject;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class PostgresDialect extends AbstractDialect {

    public PostgresDialect(Database database) {
        super(database);
    }

    @Override
    protected String aliasSeparator() {
        return " ";
    }

    @Override
    public String nextVal(SqlSequence sequence) {
        return "SELECT " + prefix(sequence) + ".nextval from dual";
    }

    @Override
    public String limit(int limit) {
        return "LIMIT " + limit;
    }


    @Override
    public String range(int offset, int limit) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public Json fromJdbcJson(ResultSet rs, int index) throws SQLException {
        return new JsonPGobject(rs.getString(index), getDatabase().jsonMapper());
    }

    @Override
    public Xml fromJdbcXml(ResultSet rs, int index) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public Xml createXml(FastByteArrayInputStream fbais) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public void setPreparedStatement(PreparedStatement ps, int index, Json json) throws SQLException {
        if (json == null) {
            ps.setNull(index, 0,"json");
            return;
        }
        if (json instanceof JsonPGobject) {
            ps.setObject(index, json);
        } else {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(new String(json.write(), StandardCharsets.UTF_8));
            ps.setObject(index, jsonObject);
        }
    }

    @Override
    public void setPreparedStatementJsonBinary(PreparedStatement ps, int index, String json) throws SQLException {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(json);
        ps.setObject(index, jsonObject);
    }

    @Override
    public void setPreparedStatementJsonBinaryNull(PreparedStatement ps, int index) throws SQLException {
        ps.setNull(index, 0,"jsonb");
    }

    @Override
    public void setPreparedStatement(PreparedStatement ps, int index, String uuid) throws SQLException {
        if (Strings.isEmpty(uuid)) {
            ps.setNull(index, Types.OTHER);
        } else {
            ps.setObject(index, uuid, Types.OTHER);
        }
    }

    @Override
    public void setPreparedStatement(PreparedStatement ps, int index, Blob blob) throws SQLException {
        ps.setBlob(index, blob);
    }

    @Override
    public void setPreparedStatement(PreparedStatement ps, int index, Clob clob) throws SQLException {
        ps.setClob(index, clob);
    }

    @Override
    public String functionJsonExists(String col, JsonPathExpression path) {
        return "jsonb_path_exists(" + col + ",'" + toSql(path) + "')";
    }

    @Override
    public String functionJsonValue(String col, JsonPathDeepExpression expression) {
        StringBuilder builder = new StringBuilder();
        builder.append(col);
        String[] fields = expression.getFields();
        if (fields.length == 1) {
            builder.append("->>'");
            builder.append(fields[0]);
            builder.append('\'');

        } else {
            int i = 0;
            for (;i < fields.length - 1; i++) {
                builder.append("->'");
                builder.append(fields[i]);
                builder.append('\'');
            }
            builder.append("->>'");
            builder.append(fields[i]);
            builder.append('\'');
        }
        return builder.toString();
    }


    @Override
    public String ilike(SqlColumn column, boolean alias) {
        StringBuilder builder = new StringBuilder(32);
        if (alias) {
            builder.append(column.table().alias()).append('.');
        }
        builder.append(column.name()).append(") ILIKE ");
        return builder.toString();
    }

    @Override
    public void init() {
        // nothing to init -> skip.
    }

    @Override
    public String toSql(Boolean value) {
        if (value == null || Boolean.FALSE == value) {
            return "false";
        } else {
            return "true";
        }
    }

    @Override
    public int getUuidType() {
        return Types.OTHER;
    }

    @Override
    public String toSql(JsonPathExpression expression) {
        PGJsonPathVisitor visitor = new PGJsonPathVisitor();
        expression.accept(visitor);
        return visitor.toString();
    }

  /*  static String rewritePath(String path) {
        String[] splits = path.split("\\.");
        StringBuilder builder = new StringBuilder();
        if (splits.length > 0) {
            if (!splits[0].equals("$")) {
                throw new IllegalStateException();
            }
            for (int i = 1 ; i < splits.length - 1; i++) {
                builder.append("->'");
                builder.append(splits[i]);
                builder.append('\'');
            }
            builder.append("->>'");
            builder.append(splits[splits.length-1]);
            builder.append('\'');
            return builder.toString();
        }
        throw new IllegalStateException();
    }*/

    private static class PGJsonPathVisitor extends AbstractJsonPathVisitor {
        @Override
        public void visit(JsonPathFieldsExpression expression) {
            getBuilder().append("?");
            expression.getExpression().accept(this);
        }
        @Override
        public void visit(JsonPathArrayExpression expression) {
            getBuilder().append("[*]");
            expression.getExpression().accept(this);
        }

    }

}