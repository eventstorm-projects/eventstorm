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
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

final class BraveResultSet implements ResultSet {

	private final ResultSet resultSet;
	private final TransactionSpan span;

	public BraveResultSet(ResultSet resultSet, TransactionSpan span) {
		this.resultSet = resultSet;
		this.span = span;
	}

	@Override
	public boolean next() throws SQLException {
		return this.resultSet.next();
	}

	@Override
	public void close() throws SQLException {
		try {
			this.resultSet.close();
		} finally {
			this.span.close();
		}

	}

	@Override
	public boolean wasNull() throws SQLException {
		return this.resultSet.wasNull();
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		String result = this.resultSet.getString(columnIndex);
		this.span.annotate(columnIndex + "->(String)->[" + result + "]");
		return result;
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		boolean result = this.resultSet.getBoolean(columnIndex);
		this.span.annotate(columnIndex + "->(boolean)->[" + result + "]");
		return result;
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		byte result = this.resultSet.getByte(columnIndex);
		this.span.annotate(columnIndex + "->(byte)->[" + result + "]");
		return result;
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		short result = this.resultSet.getShort(columnIndex);
		this.span.annotate(columnIndex + "->(short)->[" + result + "]");
		return result;
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		int result = this.resultSet.getInt(columnIndex);
		this.span.annotate(columnIndex + "->(int)->[" + result + "]");
		return result;
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		long result = this.resultSet.getLong(columnIndex);
		this.span.annotate(columnIndex + "->(long)->[" + result + "]");
		return result;
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		float result = this.resultSet.getFloat(columnIndex);
		this.span.annotate(columnIndex + "->(float)->[" + result + "]");
		return result;
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		double result = this.resultSet.getDouble(columnIndex);
		this.span.annotate(columnIndex + "->(double)->[" + result + "]");
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		BigDecimal result = this.resultSet.getBigDecimal(columnIndex, scale);
		this.span.annotate(columnIndex + "->(BigDecimal," + scale + ")->[" + result + "]");
		return result;
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		byte[] result = this.resultSet.getBytes(columnIndex);
		this.span.annotate(columnIndex + "->(byte[])->[" + result + "]");
		return result;
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		Date result = this.resultSet.getDate(columnIndex);
		this.span.annotate(columnIndex + "->(Date)->[" + result + "]");
		return result;
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		Time result = this.resultSet.getTime(columnIndex);
		this.span.annotate(columnIndex + "->(Time)->[" + result + "]");
		return result;
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		Timestamp result = this.resultSet.getTimestamp(columnIndex);
		this.span.annotate(columnIndex + "->(Timestamp)->[" + result + "]");
		return result;
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		InputStream result = this.resultSet.getAsciiStream(columnIndex);
		this.span.annotate(columnIndex + "->(AsciiStream)->[...]");
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		InputStream result = this.resultSet.getUnicodeStream(columnIndex);
		this.span.annotate(columnIndex + "->(UnicodeStream)->[...]");
		return result;
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		InputStream result = this.resultSet.getBinaryStream(columnIndex);
		this.span.annotate(columnIndex + "->(BinaryStream)->[...]");
		return result;
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		String result = this.resultSet.getString(columnLabel);
		this.span.annotate(columnLabel + "->(String)->[" + result + "]");
		return result;
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		boolean result = this.resultSet.getBoolean(columnLabel);
		this.span.annotate(columnLabel + "->(boolean)->[" + result + "]");
		return result;
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		byte result = this.resultSet.getByte(columnLabel);
		this.span.annotate(columnLabel + "->(byte)->[" + result + "]");
		return result;
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		short result = this.resultSet.getShort(columnLabel);
		this.span.annotate(columnLabel + "->(short)->[" + result + "]");
		return result;
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		int result = this.resultSet.getInt(columnLabel);
		this.span.annotate(columnLabel + "->(int)->[" + result + "]");
		return result;
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		long result = this.resultSet.getLong(columnLabel);
		this.span.annotate(columnLabel + "->(long)->[" + result + "]");
		return result;
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		float result = this.resultSet.getFloat(columnLabel);
		this.span.annotate(columnLabel + "->(float)->[" + result + "]");
		return result;
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		double result = this.resultSet.getDouble(columnLabel);
		this.span.annotate(columnLabel + "->(double)->[" + result + "]");
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		BigDecimal result = this.resultSet.getBigDecimal(columnLabel, scale);
		this.span.annotate(columnLabel + "->(BigDecimal," + scale + ")->[" + result + "]");
		return result;
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		byte[] result = this.resultSet.getBytes(columnLabel);
		this.span.annotate(columnLabel + "->(byte[])->[" + result + "]");
		return result;
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		Date result = this.resultSet.getDate(columnLabel);
		this.span.annotate(columnLabel + "->(Date)->[" + result + "]");
		return result;
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		Time result = this.resultSet.getTime(columnLabel);
		this.span.annotate(columnLabel + "->(Time)->[" + result + "]");
		return result;
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		Timestamp result = this.resultSet.getTimestamp(columnLabel);
		this.span.annotate(columnLabel + "->(Timestamp)->[" + result + "]");
		return result;
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		InputStream result = this.resultSet.getAsciiStream(columnLabel);
		this.span.annotate(columnLabel + "->(AsciiStream)->[" + result + "]");
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		InputStream result = this.resultSet.getUnicodeStream(columnLabel);
		this.span.annotate(columnLabel + "->(UnicodeStream)->[" + result + "]");
		return result;
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		InputStream result = this.resultSet.getBinaryStream(columnLabel);
		this.span.annotate(columnLabel + "->(BinaryStream)->[" + result + "]");
		return result;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return this.resultSet.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		this.resultSet.clearWarnings();
	}

	@Override
	public String getCursorName() throws SQLException {
		return this.resultSet.getCursorName();
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.resultSet.getMetaData();
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		Object result = this.resultSet.getObject(columnIndex);
		this.span.annotate(columnIndex + "->(Object)->[" + result + "]");
		return result;
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		Object result = this.resultSet.getObject(columnLabel);
		this.span.annotate(columnLabel + "->(Object)->[" + result + "]");
		return result;
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		return this.resultSet.findColumn(columnLabel);
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		Reader result = this.resultSet.getCharacterStream(columnIndex);
		this.span.annotate(columnIndex + "->(CharacterStream)->[...]");
		return result;
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		Reader result = this.resultSet.getCharacterStream(columnLabel);
		this.span.annotate(columnLabel + "->(Reader)->[" + result + "]");
		return result;
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		BigDecimal result = this.resultSet.getBigDecimal(columnIndex);
		this.span.annotate(columnIndex + "->(BigDecimal)->[" + result + "]");
		return result;
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		BigDecimal result = this.resultSet.getBigDecimal(columnLabel);
		this.span.annotate(columnLabel + "->(BigDecimal)->[" + result + "]");
		return result;
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return this.resultSet.isBeforeFirst();
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return this.resultSet.isAfterLast();
	}

	@Override
	public boolean isFirst() throws SQLException {
		return this.resultSet.isFirst();
	}

	@Override
	public boolean isLast() throws SQLException {
		return this.resultSet.isLast();
	}

	@Override
	public void beforeFirst() throws SQLException {
		this.resultSet.beforeFirst();
	}

	@Override
	public void afterLast() throws SQLException {
		this.resultSet.afterLast();
	}

	@Override
	public boolean first() throws SQLException {
		return this.resultSet.first();
	}

	@Override
	public boolean last() throws SQLException {
		return this.resultSet.last();
	}

	@Override
	public int getRow() throws SQLException {
		return this.resultSet.getRow();
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		return this.resultSet.absolute(row);
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		return this.resultSet.relative(rows);
	}

	@Override
	public boolean previous() throws SQLException {
		return this.resultSet.previous();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		this.resultSet.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return this.resultSet.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		this.resultSet.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return this.resultSet.getFetchSize();
	}

	@Override
	public int getType() throws SQLException {
		return this.resultSet.getType();
	}

	@Override
	public int getConcurrency() throws SQLException {
		return this.resultSet.getConcurrency();
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return this.resultSet.rowUpdated();
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return this.resultSet.rowInserted();
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return this.resultSet.rowDeleted();
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		this.span.annotate(columnIndex + "->(updateNull)");
		this.resultSet.updateNull(columnIndex);
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateBoolean)->[" + x + "]");
		this.resultSet.updateBoolean(columnIndex, x);
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateByte)->[" + x + "]");
		this.resultSet.updateByte(columnIndex, x);
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateShort)->[" + x + "]");
		this.resultSet.updateShort(columnIndex, x);
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateInt)->[" + x + "]");
		this.resultSet.updateInt(columnIndex, x);
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateLong)->[" + x + "]");
		this.resultSet.updateLong(columnIndex, x);
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateFloat)->[" + x + "]");
		this.resultSet.updateFloat(columnIndex, x);
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateDouble)->[" + x + "]");
		this.resultSet.updateDouble(columnIndex, x);
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateBigDecimal)->[" + x + "]");
		this.resultSet.updateBigDecimal(columnIndex, x);
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateString)->[" + x + "]");
		this.resultSet.updateString(columnIndex, x);
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateBytes)->[" + x + "]");
		this.resultSet.updateBytes(columnIndex, x);
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateDate)->[" + x + "]");
		this.resultSet.updateDate(columnIndex, x);
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateTime)->[" + x + "]");
		this.resultSet.updateTime(columnIndex, x);
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateTimestamp)->[" + x + "]");
		this.resultSet.updateTimestamp(columnIndex, x);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateAsciiStream," + length + ")->[...]");
		this.resultSet.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateBinaryStream," + length + ")->[...]");
		this.resultSet.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateCharacterStream," + length + ")->[...]");
		this.resultSet.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		this.span.annotate(columnIndex + "->(updateObject," + scaleOrLength + ")->[...]");
		this.resultSet.updateObject(columnIndex, x, scaleOrLength);
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateObject)->[" + x + "]");
		this.resultSet.updateObject(columnIndex, x);
	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		this.span.annotate(columnLabel + "->(updateNull)");
		this.resultSet.updateNull(columnLabel);
	}

	@Override
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateBoolean)->[" + x + "]");
		this.resultSet.updateBoolean(columnLabel, x);
	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateByte)->[" + x + "]");
		this.resultSet.updateByte(columnLabel, x);
	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateShort)->[" + x + "]");
		this.resultSet.updateShort(columnLabel, x);
	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateInt)->[" + x + "]");
		this.resultSet.updateInt(columnLabel, x);
	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateLong)->[" + x + "]");
		this.resultSet.updateLong(columnLabel, x);
	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateFloat)->[" + x + "]");
		this.resultSet.updateFloat(columnLabel, x);
	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateDouble)->[" + x + "]");
		this.resultSet.updateDouble(columnLabel, x);
	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateBigDecimal)->[" + x + "]");
		this.resultSet.updateBigDecimal(columnLabel, x);

	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateString)->[" + x + "]");
		this.resultSet.updateString(columnLabel, x);
	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateBytes)->[" + x + "]");
		this.resultSet.updateBytes(columnLabel, x);
	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateDate)->[" + x + "]");
		this.resultSet.updateDate(columnLabel, x);
	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateTime)->[" + x + "]");
		this.resultSet.updateTime(columnLabel, x);
	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateTimestamp)->[" + x + "]");
		this.resultSet.updateTimestamp(columnLabel, x);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateAsciiStream," + length + ")->[...]");
		this.resultSet.updateAsciiStream(columnLabel, x, length);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateBinaryStream," + length + ")->[...]");
		this.resultSet.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateCharacterStream," + length + ")->[...]");
		this.resultSet.updateCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		this.span.annotate(columnLabel + "->(updateObject," + scaleOrLength + ")->[" + x + "]");
		this.resultSet.updateObject(columnLabel, x, scaleOrLength);
	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateObject)->[" + x + "]");
		this.resultSet.updateObject(columnLabel, x);
	}

	@Override
	public void insertRow() throws SQLException {
		this.resultSet.insertRow();
	}

	@Override
	public void updateRow() throws SQLException {
		this.resultSet.updateRow();
	}

	@Override
	public void deleteRow() throws SQLException {
		this.resultSet.deleteRow();
	}

	@Override
	public void refreshRow() throws SQLException {
		this.resultSet.refreshRow();
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		this.resultSet.cancelRowUpdates();
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		this.resultSet.moveToInsertRow();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		this.resultSet.moveToCurrentRow();
	}

	@Override
	public Statement getStatement() throws SQLException {
		return this.resultSet.getStatement();
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		Object result = this.resultSet.getObject(columnIndex, map);
		this.span.annotate(columnIndex + "->(Object, " + map + ")->[" + result + "]");
		return result;
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		Ref result = this.resultSet.getRef(columnIndex);
		this.span.annotate(columnIndex + "->(Ref)->[" + result + "]");
		return result;
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		Blob result = this.resultSet.getBlob(columnIndex);
		this.span.annotate(columnIndex + "->(Blob)->[...]");
		return result;
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		Clob result = this.resultSet.getClob(columnIndex);
		this.span.annotate(columnIndex + "->(Clob)->[...]");
		return result;
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		Array result = this.resultSet.getArray(columnIndex);
		this.span.annotate(columnIndex + "->(Array)->[" + result + "]");
		return result;
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		Object result = this.resultSet.getObject(columnLabel, map);
		this.span.annotate(columnLabel + "->(Object, " + map + ")->[" + result + "]");
		return result;
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		Ref result = this.resultSet.getRef(columnLabel);
		this.span.annotate(columnLabel + "->(Ref)->[" + result + "]");
		return result;
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		Blob result = this.resultSet.getBlob(columnLabel);
		this.span.annotate(columnLabel + "->(Blob)->[" + result + "]");
		return result;
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		Clob result = this.resultSet.getClob(columnLabel);
		this.span.annotate(columnLabel + "->(Clob)->[" + result + "]");
		return result;
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		Array result = this.resultSet.getArray(columnLabel);
		this.span.annotate(columnLabel + "->(Array)->[" + result + "]");
		return result;
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		Date result = this.resultSet.getDate(columnIndex, cal);
		this.span.annotate(columnIndex + "->(Date," + cal + ")->[" + result + "]");
		return result;
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		Date result = this.resultSet.getDate(columnLabel, cal);
		this.span.annotate(columnLabel + "->(Date," + cal + ")->[" + result + "]");
		return result;
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		Time result = this.resultSet.getTime(columnIndex, cal);
		this.span.annotate(columnIndex + "->(Time," + cal + ")->[" + result + "]");
		return result;
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		Time result = this.resultSet.getTime(columnLabel, cal);
		this.span.annotate(columnLabel + "->(Time," + cal + ")->[" + result + "]");
		return result;
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		Timestamp result = this.resultSet.getTimestamp(columnIndex, cal);
		this.span.annotate(columnIndex + "->(Timestamp," + cal + ")->[" + result + "]");
		return result;
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		Timestamp result = this.resultSet.getTimestamp(columnLabel, cal);
		this.span.annotate(columnLabel + "->(Timestamp," + cal + ")->[" + result + "]");
		return result;
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		URL result = this.resultSet.getURL(columnIndex);
		this.span.annotate(columnIndex + "->(String)->[" + result + "]");
		return result;
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		URL result = this.resultSet.getURL(columnLabel);
		this.span.annotate(columnLabel + "->(URL)->[" + result + "]");
		return result;
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateRef)->[" + x + "]");
		this.resultSet.updateRef(columnIndex, x);
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateRef)->[" + x + "]");
		this.resultSet.updateRef(columnLabel, x);
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateBlob)->[" + x + "]");
		this.resultSet.updateBlob(columnIndex, x);
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateBlob)->[" + x + "]");
		this.resultSet.updateBlob(columnLabel, x);
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateClob)->[" + x + "]");
		this.resultSet.updateClob(columnIndex, x);
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateClob)->[" + x + "]");
		this.resultSet.updateClob(columnLabel, x);
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateArray)->[" + x + "]");
		this.resultSet.updateArray(columnIndex, x);
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateArray)->[" + x + "]");
		this.resultSet.updateArray(columnLabel, x);
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		RowId result = this.resultSet.getRowId(columnIndex);
		this.span.annotate(columnIndex + "->(RowId)->[" + result + "]");
		return result;
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		RowId result = this.resultSet.getRowId(columnLabel);
		this.span.annotate(columnLabel + "->(RowId)->[" + result + "]");
		return result;
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateRowId)->[" + x + "]");
		this.resultSet.updateRowId(columnIndex, x);
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateRowId)->[" + x + "]");
		this.resultSet.updateRowId(columnLabel, x);
	}

	@Override
	public int getHoldability() throws SQLException {
		return this.resultSet.getHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return this.resultSet.isClosed();
	}

	@Override
	public void updateNString(int columnIndex, String x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateNString)->[" + x + "]");
		this.resultSet.updateNString(columnIndex, x);
	}

	@Override
	public void updateNString(String columnLabel, String x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateArray)->[" + x + "]");
		this.resultSet.updateNString(columnLabel, x);
	}

	@Override
	public void updateNClob(int columnIndex, NClob x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateNClob)->[" + x + "]");
		this.resultSet.updateNClob(columnIndex, x);
	}

	@Override
	public void updateNClob(String columnLabel, NClob x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateNClob)->[" + x + "]");
		this.resultSet.updateNClob(columnLabel, x);
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		NClob result = this.resultSet.getNClob(columnIndex);
		this.span.annotate(columnIndex + "->(NClob)->[...]");
		return result;
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		NClob result = this.resultSet.getNClob(columnLabel);
		this.span.annotate(columnLabel + "->(NClob)->[...]");
		return result;
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		SQLXML result = this.resultSet.getSQLXML(columnIndex);
		this.span.annotate(columnIndex + "->(SQLXML)->[" + result + "]");
		return result;
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		SQLXML result = this.resultSet.getSQLXML(columnLabel);
		this.span.annotate(columnLabel + "->(SQLXML)->[" + result + "]");
		return result;
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateSQLXML)->[" + x + "]");
		this.resultSet.updateSQLXML(columnIndex, x);
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateSQLXML)->[" + x + "]");
		this.resultSet.updateSQLXML(columnLabel, x);
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		String result = this.resultSet.getNString(columnIndex);
		this.span.annotate(columnIndex + "->(NString)->[" + result + "]");
		return result;
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		String result = this.resultSet.getNString(columnLabel);
		this.span.annotate(columnLabel + "->(NString)->[" + result + "]");
		return result;
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		Reader result = this.resultSet.getNCharacterStream(columnIndex);
		this.span.annotate(columnIndex + "->(NCharacterStream)->[...]");
		return result;
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		Reader result = this.resultSet.getNCharacterStream(columnLabel);
		this.span.annotate(columnLabel + "->(NCharacterStream)->[...]");
		return result;
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateNCharacterStream," + length + ")->[" + x + "]");
		this.resultSet.updateNCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateNCharacterStream," + length + ")->[" + x + "]");
		this.resultSet.updateNCharacterStream(columnLabel, x, length);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateAsciiStream," + length + ")->[" + x + "]");
		this.resultSet.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateBinaryStream," + length + ")->[" + x + "]");
		this.resultSet.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateCharacterStream," + length + ")->[" + x + "]");
		this.resultSet.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateAsciiStream," + length + ")->[" + x + "]");
		this.resultSet.updateAsciiStream(columnLabel, x, length);

	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateBinaryStream," + length + ")->[" + x + "]");
		this.resultSet.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateCharacterStream," + length + ")->[" + x + "]");
		this.resultSet.updateCharacterStream(columnLabel, x, length);
	}

	@Override
	public void updateBlob(int columnIndex, InputStream x, long length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateBlob," + length + ")->[" + x + "]");
		this.resultSet.updateBlob(columnIndex, x, length);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream x, long length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateBlob," + length + ")->[" + x + "]");
		this.resultSet.updateBlob(columnLabel, x, length);
	}

	@Override
	public void updateClob(int columnIndex, Reader x, long length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateClob," + length + ")->[" + x + "]");
		this.resultSet.updateClob(columnIndex, x, length);
	}

	@Override
	public void updateClob(String columnLabel, Reader x, long length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateClob," + length + ")->[" + x + "]");
		this.resultSet.updateClob(columnLabel, x, length);
	}

	@Override
	public void updateNClob(int columnIndex, Reader x, long length) throws SQLException {
		this.span.annotate(columnIndex + "->(updateNClob," + length + ")->[" + x + "]");
		this.resultSet.updateNClob(columnIndex, x, length);
	}

	@Override
	public void updateNClob(String columnLabel, Reader x, long length) throws SQLException {
		this.span.annotate(columnLabel + "->(updateNClob," + length + ")->[" + x + "]");
		this.resultSet.updateNClob(columnLabel, x, length);
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateNCharacterStream)->[" + x + "]");
		this.resultSet.updateNCharacterStream(columnIndex, x);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateNCharacterStream)->[" + x + "]");
		this.resultSet.updateNCharacterStream(columnLabel, x);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateAsciiStream)->[" + x + "]");
		this.resultSet.updateAsciiStream(columnIndex, x);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateBinaryStream)->[" + x + "]");
		this.resultSet.updateBinaryStream(columnIndex, x);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateCharacterStream)->[" + x + "]");
		this.resultSet.updateCharacterStream(columnIndex, x);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateAsciiStream)->[" + x + "]");
		this.resultSet.updateAsciiStream(columnLabel, x);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateBinaryStream)->[" + x + "]");
		this.resultSet.updateBinaryStream(columnLabel, x);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateCharacterStream)->[" + x + "]");
		this.resultSet.updateCharacterStream(columnLabel, x);
	}

	@Override
	public void updateBlob(int columnIndex, InputStream x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateBlob)->[" + x + "]");
		this.resultSet.updateBlob(columnIndex, x);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateBlob)->[" + x + "]");
		this.resultSet.updateBlob(columnLabel, x);
	}

	@Override
	public void updateClob(int columnIndex, Reader x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateClob)->[" + x + "]");
		this.resultSet.updateClob(columnIndex, x);
	}

	@Override
	public void updateClob(String columnLabel, Reader x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateClob)->[" + x + "]");
		this.resultSet.updateClob(columnLabel, x);
	}

	@Override
	public void updateNClob(int columnIndex, Reader x) throws SQLException {
		this.span.annotate(columnIndex + "->(updateNClob)->[" + x + "]");
		this.resultSet.updateNClob(columnIndex, x);
	}

	@Override
	public void updateNClob(String columnLabel, Reader x) throws SQLException {
		this.span.annotate(columnLabel + "->(updateNClob)->[" + x + "]");
		this.resultSet.updateNClob(columnLabel, x);
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		T result = this.resultSet.getObject(columnIndex, type);
		this.span.annotate(columnIndex + "->(Object," + type + ")->[" + result + "]");
		return result;
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		T result = this.resultSet.getObject(columnLabel, type);
		this.span.annotate(columnLabel + "->(Object," + type + ")->[" + result + "]");
		return result;
	}

	// From interface Wrapper

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.resultSet.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.resultSet.isWrapperFor(iface);
	}

}
