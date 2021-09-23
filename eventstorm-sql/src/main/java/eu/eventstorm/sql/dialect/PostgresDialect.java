package eu.eventstorm.sql.dialect;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Xml;
import eu.eventstorm.sql.type.postgres.JsonPGobject;
import eu.eventstorm.util.FastByteArrayInputStream;
import org.postgresql.util.PGobject;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public Xml fromJdbcXml(ResultSet rs, int index) throws SQLException {
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
            jsonObject.setValue(new String(json.write(getDatabase().jsonMapper()), StandardCharsets.UTF_8));
            ps.setObject(index, jsonObject);
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
    public String functionJsonExists(String col, String path) {
        throw new UnsupportedOperationException("to implement");
    }

    @Override
    public String functionJsonValue(String col, String path) {
        throw new UnsupportedOperationException("to implement");
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

}