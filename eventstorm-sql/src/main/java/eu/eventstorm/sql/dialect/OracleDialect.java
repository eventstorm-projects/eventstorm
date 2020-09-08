package eu.eventstorm.sql.dialect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Xml;
import eu.eventstorm.sql.type.common.AbstractBlob;
import eu.eventstorm.sql.type.common.AbstractClob;
import eu.eventstorm.sql.type.common.BlobJson;
import eu.eventstorm.sql.type.common.BlobXml;
import eu.eventstorm.util.FastByteArrayInputStream;
import eu.eventstorm.util.Streams;

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
    public String range(int offset, int limit) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }
    
	@Override
	public String range() {
		return "LIMIT ? OFFSET ?";
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
		ps.setString(index, new String(json.write(this.getDatabase().jsonMapper()), StandardCharsets.UTF_8));
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
			try (InputStream is = clob.getAsciiStream()) {
				try (OutputStream os = oracleClob.setAsciiStream(1)) {
					Streams.copy(is, os);	
				}
			} catch (IOException cause) {
				throw new IllegalStateException(cause);
			}
		} else {
			oracleClob = clob;
		}
		ps.setClob(index, oracleClob);
	}

}