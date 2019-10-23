package eu.eventstorm.sql.dialect;

import static com.google.common.collect.ImmutableMap.of;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Xml;
import eu.eventstorm.sql.type.common.BlobJson;
import eu.eventstorm.sql.type.common.BlobXml;
import eu.eventstorm.util.FastByteArrayInputStream;

final class H2Dialect extends AbstractDialect {

    public H2Dialect(Database database) {
        super(database);
    }

    @Override
    protected String aliasSeparator() {
        return " AS ";
    }

    @Override
    public String nextVal(SqlSequence sequence) {
        return "SELECT NEXTVAL('" + prefix(sequence) + "')";
    }

    @Override
    public String range(int offset, int limit) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }

	@Override
	public Json createJson(Map<String, Object> value) {
		try {
			return new BlobJson(value);
		} catch (IOException cause) {
			throw new EventstormDialectException(EventstormDialectException.Type.FAILED_TO_WRITE_JSON, of("map", value), cause);
		}
	}
	
	@Override
	public Json createJson(byte[] value) {
		return new BlobJson(value);
	}

	@Override
	public Json fromJdbcJson(ResultSet rs, int index) throws SQLException {
		return new BlobJson(rs.getBytes(index));
	}

	@Override
	public Xml fromJdbcXml(ResultSet rs, int index) throws SQLException {
		return new BlobXml(rs.getBytes(index));
	}

	@Override
	public Xml createXml(FastByteArrayInputStream fbais) {
		return new BlobXml(fbais.readAll());
	}
	
}
