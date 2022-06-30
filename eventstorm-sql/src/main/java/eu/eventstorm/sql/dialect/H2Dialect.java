package eu.eventstorm.sql.dialect;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.RawSqlExecutor;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.expression.JsonPathArrayExpression;
import eu.eventstorm.sql.expression.JsonPathDeepExpression;
import eu.eventstorm.sql.expression.JsonPathExpression;
import eu.eventstorm.sql.expression.JsonPathFieldStringExpression;
import eu.eventstorm.sql.expression.JsonPathFieldsExpression;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Xml;
import eu.eventstorm.sql.type.common.BlobJson;
import eu.eventstorm.sql.type.common.BlobXml;
import eu.eventstorm.util.FastByteArrayInputStream;
import eu.eventstorm.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class H2Dialect extends AbstractDialect {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2Dialect.class);

    public H2Dialect(Database database) {
        super(database);
    }

    @Override
    protected String aliasSeparator() {
        return " ";
    }

    @Override
    public String nextVal(SqlSequence sequence) {
        return "SELECT NEXTVAL('" + prefix(sequence) + "')";
    }

    @Override
    public String limit(int limit) {
        return "LIMIT " + limit;
    }

    @Override
    public String range(int offset, int limit) {
        return "OFFSET " + offset + " ROWS FETCH FIRST " + limit + " ROWS ONLY";
    }

    @Override
    public Json fromJdbcJson(ResultSet rs, int index) throws SQLException {
        return new BlobJson(getDatabase().jsonMapper(), rs.getBytes(index));
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
            ps.setBytes(index, json.write());
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
    public void setPreparedStatement(PreparedStatement ps, int index, String uuid) throws SQLException {
        if (Strings.isEmpty(uuid)) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, uuid);
        }
    }

    public void init() {
        RawSqlExecutor sql = getDatabase().rawSqlExecutor();
        try {
            sql.execute("CREATE ALIAS IF NOT EXISTS json_exists FOR \" eu.eventstorm.sql.util.H2Functions.json_exists\";");
            sql.execute("CREATE ALIAS IF NOT EXISTS json_value FOR \" eu.eventstorm.sql.util.H2Functions.json_value\";");
        } catch (SQLException cause) {
            throw new IllegalStateException(cause);
        }
    }

    @Override
    public String functionJsonExists(String col, JsonPathExpression path) {
        return "json_exists(" + col + ",'" + toSql(path) + "')";
    }

    @Override
    public String functionJsonValue(String col, JsonPathDeepExpression path) {
        return "json_value(" + col + ",'" + path + "')";
    }

    @Override
    public String ilike(SqlColumn column, boolean alias) {
        StringBuilder builder = new StringBuilder(32);
        builder.append("UPPER(");
        if (alias) {
            builder.append(column.table().alias()).append('.');
        }
        builder.append(column.name()).append(") LIKE UPPER(?)");
        return builder.toString();
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
    public String toSql(JsonPathExpression expression) {
        H2JsonPathVisitor visitor = new H2JsonPathVisitor();
        expression.accept(visitor);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("toSql()->[{}]",visitor);
        }

        return visitor.toString();
    }

    private static class H2JsonPathVisitor extends AbstractJsonPathVisitor {
        @Override
        public void visit(JsonPathFieldsExpression expression) {
            getBuilder().append(".[?");
            expression.getExpression().accept(this);
            getBuilder().append("]");
        }
        @Override
        public void visit(JsonPathArrayExpression expression) {
            getBuilder().append(".[");
            expression.getExpression().accept(this);
            getBuilder().append("]");
        }

    }

}