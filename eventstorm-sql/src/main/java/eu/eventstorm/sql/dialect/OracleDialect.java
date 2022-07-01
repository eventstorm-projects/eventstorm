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
import eu.eventstorm.sql.type.common.AbstractBlob;
import eu.eventstorm.sql.type.common.AbstractClob;
import eu.eventstorm.sql.type.common.BlobXml;
import eu.eventstorm.sql.type.common.StringJson;
import eu.eventstorm.util.FastByteArrayInputStream;
import eu.eventstorm.util.Streams;
import eu.eventstorm.util.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class OracleDialect extends AbstractDialect {

    public OracleDialect(Database database) {
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
        return "FETCH NEXT " + limit + " ROWS ONLY";
    }


    @Override
    public String range(int offset, int limit) {
        return "OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
    }

    @Override
    public Json fromJdbcJson(ResultSet rs, int index) throws SQLException {
        String value = rs.getString(index);
        if (Strings.isEmpty(value)) {
            return null;
        } else {
            return new StringJson(getDatabase().jsonMapper(), value);
        }
    }

    @Override
    public Xml fromJdbcXml(ResultSet rs, int index) throws SQLException {
        return new BlobXml(rs.getBytes(index));
    }

    @Override
    public Xml createXml(FastByteArrayInputStream fbais) {
        return new BlobXml(fbais.readAll());
    }

    @Override
    public void setPreparedStatement(PreparedStatement ps, int index, Json json) throws SQLException {
        if (json == null) {
            ps.setNull(index, Types.CLOB);
        } else {
            ps.setString(index, json.writeAsString());
        }
    }

    @Override
    public void setPreparedStatement(PreparedStatement ps, int index, Blob blob) throws SQLException {
        Blob oracleBlob;
        if (blob instanceof AbstractBlob) {
            oracleBlob = ps.getConnection().createBlob();
            try (InputStream is = blob.getBinaryStream()) {
                try (OutputStream os = oracleBlob.setBinaryStream(1)) {
                    Streams.copy(is, os);
                }
            } catch (IOException cause) {
                throw new IllegalStateException(cause);
            }
        } else {
            oracleBlob = blob;
        }
        ps.setBlob(index, oracleBlob);
    }

    @Override
    public void setPreparedStatement(PreparedStatement ps, int index, Clob clob) throws SQLException {
        Clob oracleClob;
        if (clob instanceof AbstractClob) {
            oracleClob = ps.getConnection().createClob();
            try (Reader reader = clob.getCharacterStream()) {
                try (Writer writer = oracleClob.setCharacterStream(1)) {
                    Streams.copy(reader, writer);
                }
            } catch (IOException cause) {
                throw new IllegalStateException(cause);
            }
        } else {
            oracleClob = clob;
        }
        ps.setClob(index, oracleClob);
    }

    @Override
    public void setPreparedStatement(PreparedStatement ps, int index, String uuid) throws SQLException {
        if (Strings.isEmpty(uuid)) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, uuid);
        }
    }

    @Override
    public String functionJsonExists(String col, JsonPathExpression path) {
    //    String rewritePath = rewritePath(path);
        return "json_exists(" + col + ",'" + toSql(path) + "')";
    }

    @Override
    public String functionJsonValue(String col, JsonPathDeepExpression expression) {
        StringBuilder builder = new StringBuilder();
        builder.append('$');
        String[] fields = expression.getFields();
        for (String field : fields) {
            builder.append('.').append(field);
        }
        return "json_value(" + col + ",'" + builder + "')";
    }

    @Override
    public String ilike(SqlColumn column, boolean alias) {
        StringBuilder builder = new StringBuilder(32);
        builder.append("UPPER(");
        if (alias) {
            builder.append(column.table().alias()).append('.');
        }
        builder.append(column.name()).append(") LIKE ? UPPER(");
        return builder.toString();
    }

    @Override
    public void init() {
        // nothing to init -> skip.
    }

    @Override
    public int getBooleanType() {
        return Types.INTEGER;
    }

    @Override
    public String toSql(Boolean value) {
        if (value == null || Boolean.FALSE == value) {
            return "0";
        } else {
            return "1";
        }
    }

    @Override
    public String toSql(JsonPathExpression expression) {
        OracleJsonPathVisitor visitor = new OracleJsonPathVisitor();
        expression.accept(visitor);
        return visitor.toString();
    }

    private static class OracleJsonPathVisitor extends AbstractJsonPathVisitor {
        @Override
        public void visit(JsonPathFieldsExpression expression) {
            getBuilder().append(".[?");
            expression.getExpression().accept(this);
            getBuilder().append("]");
        }
        @Override
        public void visit(JsonPathArrayExpression expression) {
            expression.getExpression().accept(this);
        }

    }
}