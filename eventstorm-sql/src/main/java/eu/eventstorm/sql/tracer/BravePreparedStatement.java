package eu.eventstorm.sql.tracer;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

final class BravePreparedStatement extends BraveStatement implements PreparedStatement {

	public static final String SET_BINARY = ",...)";
	public static final String SET_BINARY_3 = ",...,";

	
	private final PreparedStatement ps;

	BravePreparedStatement(PreparedStatement ps, BraveTracer tracer) {
		super(ps, tracer);
		this.ps = ps;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		return new BraveResultSet(ps.executeQuery(), span("ps.executeQuery"));

	}

	@Override
	public int executeUpdate() throws SQLException {
		return this.ps.executeUpdate();
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		annotate("setNull(" + parameterIndex + "," + sqlType + ")");
		this.ps.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		annotate("setBoolean(" + parameterIndex + "," + x + ")");
		this.ps.setBoolean(parameterIndex, x);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		annotate("setByte(" + parameterIndex + "," + x + ")");
		this.ps.setByte(parameterIndex, x);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		annotate("setShort(" + parameterIndex + "," + x + ")");
		this.ps.setShort(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		annotate("setInt(" + parameterIndex + "," + x + ")");
		this.ps.setInt(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		annotate("setLong(" + parameterIndex + "," + x + ")");
		this.ps.setLong(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		annotate("setFloat(" + parameterIndex + "," + x + ")");
		this.ps.setFloat(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		annotate("setDouble(" + parameterIndex + "," + x + ")");
		this.ps.setDouble(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		annotate("setBigDecimal(" + parameterIndex + "," + x + ")");
		this.ps.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		annotate("setString(" + parameterIndex + "," + x + ")");
		this.ps.setString(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		annotate("setBytes(" + parameterIndex + SET_BINARY);
		this.ps.setBytes(parameterIndex, x);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		annotate("setDate(" + parameterIndex + "," + x + ")");
		this.ps.setDate(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		annotate("setTime(" + parameterIndex + "," + x + ")");
		this.ps.setTime(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		annotate("setTimestamp(" + parameterIndex + "," + x + ")");
		this.ps.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		annotate("setAsciiStream(" + parameterIndex + SET_BINARY_3 + length + ")");
		this.ps.setAsciiStream(parameterIndex, x, length);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		annotate("setUnicodeStream(" + parameterIndex + SET_BINARY_3 + length + ")");
		this.ps.setUnicodeStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		annotate("setBinaryStream(" + parameterIndex + SET_BINARY_3 + length + ")");
		this.ps.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void clearParameters() throws SQLException {
		this.ps.clearParameters();
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		annotate("setObject(" + parameterIndex + SET_BINARY_3 + targetSqlType + ")");
		this.ps.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		annotate("setObject(" + parameterIndex + "," + x + ")");
		this.ps.setObject(parameterIndex, x);
	}

	@Override
	public boolean execute() throws SQLException {
		return this.ps.execute();
	}

	@Override
	public void addBatch() throws SQLException {
		this.ps.addBatch();
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		annotate("setCharacterStream(" + parameterIndex + SET_BINARY_3 + length + ")");
		this.ps.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		annotate("setRef(" + parameterIndex + "," + x + ")");
		this.ps.setRef(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		annotate("setBlob(" + parameterIndex + SET_BINARY);
		this.ps.setBlob(parameterIndex, x);
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		annotate("setClob(" + parameterIndex + SET_BINARY);
		this.ps.setClob(parameterIndex, x);
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		annotate("setArray(" + parameterIndex + SET_BINARY);
		this.ps.setArray(parameterIndex, x);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.ps.getMetaData();
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		annotate("setDate(" + parameterIndex + "," + x + "," + cal +")");
		this.ps.setDate(parameterIndex, x, cal);
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		annotate("setTime(" + parameterIndex + "," + x + "," + cal +")");
		this.ps.setTime(parameterIndex, x, cal);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		annotate("setTimestamp(" + parameterIndex + "," + x + "," + cal +")");
		this.ps.setTimestamp(parameterIndex, x, cal);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		annotate("setNull(" + parameterIndex + "," + sqlType + "," + typeName +")");
		this.ps.setNull(parameterIndex, sqlType, typeName);
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		annotate("setURL(" + parameterIndex + "," + x + ")");
		this.ps.setURL(parameterIndex, x);
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return this.ps.getParameterMetaData();
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		annotate("setRowId(" + parameterIndex + "," + x + ")");
		this.ps.setRowId(parameterIndex, x);
	}

	@Override
	public void setNString(int parameterIndex, String x) throws SQLException {
		annotate("setNString(" + parameterIndex + "," + x + ")");
		this.ps.setNString(parameterIndex, x);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		annotate("setNCharacterStream(" + parameterIndex + SET_BINARY_3+ length +")");
		this.ps.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		annotate("setNClob(" + parameterIndex + SET_BINARY);
		this.ps.setNClob(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		annotate("setClob(" + parameterIndex + SET_BINARY_3+ length +")");
		this.ps.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		annotate("setBlob(" + parameterIndex + SET_BINARY_3+ length +")");
		this.ps.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		annotate("setNClob(" + parameterIndex + SET_BINARY_3+ length +")");
		this.ps.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML x) throws SQLException {
		annotate("setSQLXML(" + parameterIndex + "," + x + ")");
		this.ps.setSQLXML(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		annotate("setObject(" + parameterIndex + SET_BINARY_3+ targetSqlType + ","+ scaleOrLength +")");
		this.ps.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		annotate("setAsciiStream(" + parameterIndex + SET_BINARY_3+ length +")");
		this.ps.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		annotate("setBinaryStream(" + parameterIndex + SET_BINARY_3+ length +")");
		this.ps.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		annotate("setCharacterStream(" + parameterIndex + SET_BINARY_3+ length +")");
		this.ps.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		annotate("setAsciiStream(" + parameterIndex + SET_BINARY);
		this.ps.setAsciiStream(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		annotate("setBinaryStream(" + parameterIndex + SET_BINARY);
		this.ps.setBinaryStream(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		annotate("setCharacterStream(" + parameterIndex + SET_BINARY);
		this.ps.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		annotate("setNCharacterStream(" + parameterIndex + SET_BINARY);
		this.ps.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		annotate("setClob(" + parameterIndex + SET_BINARY);
		this.ps.setClob(parameterIndex, reader);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		annotate("setBlob(" + parameterIndex + SET_BINARY);
		this.ps.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		annotate("setNClob(" + parameterIndex + SET_BINARY);
		this.ps.setNClob(parameterIndex, reader);
	}

	@Override
	public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
		annotate("setObject(" + parameterIndex + SET_BINARY_3 + targetSqlType +")");
		this.ps.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		annotate("setObject(" + parameterIndex + SET_BINARY_3 + targetSqlType + ","+ scaleOrLength+")");
		this.ps.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

}
