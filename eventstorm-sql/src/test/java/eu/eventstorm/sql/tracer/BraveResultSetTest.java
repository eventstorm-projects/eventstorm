package eu.eventstorm.sql.tracer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.CharStreams;

import brave.Tracer;
import brave.Tracing;
import brave.sampler.Sampler;
import eu.eventstorm.sql.tracer.BraveResultSet;
import eu.eventstorm.sql.tracer.LoggingBraveReporter;
import eu.eventstorm.sql.tracer.TransactionTracer;
import eu.eventstorm.sql.tracer.TransactionTracers;
import eu.eventstorm.util.Streams;

class BraveResultSetTest {
	
	private TransactionTracer tracer;
	
	@BeforeEach
	void before() {
		Tracer tracer = Tracing.newBuilder().sampler(Sampler.ALWAYS_SAMPLE).spanReporter(new LoggingBraveReporter()).build().tracer();
		this.tracer = TransactionTracers.brave(tracer);
	}

	@Test
	void testGetString() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getString(1)).thenReturn("hello");
		when(resultSet.getString("hello")).thenReturn("hello");

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("samplz"))) {
			assertEquals("hello", rs.getString(1));
			assertEquals("hello", rs.getString("hello"));
		}
	}
	
	@Test
	void testGetNString() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getNString(1)).thenReturn("hello");
		when(resultSet.getNString("hello")).thenReturn("hello");

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals("hello", rs.getNString(1));
			assertEquals("hello", rs.getNString("hello"));
		}
	}

	@Test
	void testGetBoolean() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getBoolean(1)).thenReturn(true);
		when(resultSet.getBoolean("hello")).thenReturn(false);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(true, rs.getBoolean(1));
			assertEquals(false, rs.getBoolean("hello"));
		}
	}

	@Test
	void testGetByte() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getByte(1)).thenReturn((byte) 1);
		when(resultSet.getByte("hello")).thenReturn((byte) 2);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals((byte) 1, rs.getByte(1));
			assertEquals((byte) 2, rs.getByte("hello"));
		}
	}

	@Test
	void testGetShort() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getShort(1)).thenReturn((short) 1);
		when(resultSet.getShort("hello")).thenReturn((short) 2);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals((short) 1, rs.getShort(1));
			assertEquals((short) 2, rs.getShort("hello"));
		}
	}

	@Test
	void testGetInt() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getInt(1)).thenReturn(1);
		when(resultSet.getInt("hello")).thenReturn(2);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(1, rs.getInt(1));
			assertEquals(2, rs.getInt("hello"));
		}
	}

	@Test
	void testGetLong() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getLong(1)).thenReturn(1L);
		when(resultSet.getLong("hello")).thenReturn(2L);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(1L, rs.getLong(1));
			assertEquals(2L, rs.getLong("hello"));
		}
	}

	@Test
	void testGetFloat() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getFloat(1)).thenReturn(1.1f);
		when(resultSet.getFloat("hello")).thenReturn(12f);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(1.1f, rs.getFloat(1));
			assertEquals(12f, rs.getFloat("hello"));
		}
	}

	@Test
	void testGetDouble() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getDouble(1)).thenReturn(1.1d);
		when(resultSet.getDouble("hello")).thenReturn(1.2d);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(1.1d, rs.getDouble(1));
			assertEquals(1.2d, rs.getDouble("hello"));
		}
	}

	@Test
	void testGetBytes() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getBytes(1)).thenReturn(new byte[] { (byte) 1, (byte) 2 });
		when(resultSet.getBytes("hello")).thenReturn(new byte[] { (byte) 3, (byte) 4 });

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertArrayEquals(new byte[] { (byte) 1, (byte) 2 }, rs.getBytes(1));
			assertArrayEquals(new byte[] { (byte) 3, (byte) 4 }, rs.getBytes("hello"));
		}
	}

	@Test
	void testGetDate() throws SQLException {

		Date date1 = Date.valueOf(LocalDate.of(2014, 9, 17));
		Date date2 = Date.valueOf(LocalDate.of(2011, 3, 9));
		Calendar calendar = Calendar.getInstance();

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getDate(1)).thenReturn(date1);
		when(resultSet.getDate("hello")).thenReturn(date2);
		when(resultSet.getDate(12, calendar)).thenReturn(date1);
		when(resultSet.getDate("hello12", calendar)).thenReturn(date2);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(date1, rs.getDate(1));
			assertEquals(date2, rs.getDate("hello"));
			assertEquals(date1, rs.getDate(12, calendar));
			assertEquals(date2, rs.getDate("hello12", calendar));
		}
	}

	@Test
	void testGetTime() throws SQLException {

		Time date1 = Time.valueOf(LocalTime.of(12, 30, 50));
		Time date2 = Time.valueOf(LocalTime.of(12, 40, 50));
		Calendar calendar = Calendar.getInstance();
		
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getTime(1)).thenReturn(date1);
		when(resultSet.getTime("hello")).thenReturn(date2);
		when(resultSet.getTime(12, calendar)).thenReturn(date1);
		when(resultSet.getTime("hello12", calendar)).thenReturn(date2);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(date1, rs.getTime(1));
			assertEquals(date2, rs.getTime("hello"));
			assertEquals(date1, rs.getTime(12, calendar));
			assertEquals(date2, rs.getTime("hello12", calendar));
		}
	}

	@Test
	void testGetTimestamp() throws SQLException {

		Timestamp date1 = Timestamp.valueOf(LocalDateTime.of(2014, 9, 17, 0, 0, 0));
		Timestamp date2 = Timestamp.valueOf(LocalDateTime.of(2011, 3, 9, 0, 0, 0));
		Calendar calendar = Calendar.getInstance();
		
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getTimestamp(1)).thenReturn(date1);
		when(resultSet.getTimestamp("hello")).thenReturn(date2);
		when(resultSet.getTimestamp(12, calendar)).thenReturn(date1);
		when(resultSet.getTimestamp("hello12", calendar)).thenReturn(date2);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(date1, rs.getTimestamp(1));
			assertEquals(date2, rs.getTimestamp("hello"));
			assertEquals(date1, rs.getTimestamp(12, calendar));
			assertEquals(date2, rs.getTimestamp("hello12", calendar));
		}
	}

	@Test
	void testGetAsciiStream() throws SQLException, IOException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getAsciiStream(1)).thenReturn(new ByteArrayInputStream("hello".getBytes()));
		when(resultSet.getAsciiStream("hello")).thenReturn(new ByteArrayInputStream("world".getBytes()));

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals("hello", new String(Streams.copyToByteArray(rs.getAsciiStream(1))));
			assertEquals("world", new String(Streams.copyToByteArray(rs.getAsciiStream("hello"))));
		}
	}

	@Test
	void testGetBinaryStream() throws SQLException, IOException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getBinaryStream(1)).thenReturn(new ByteArrayInputStream("hello".getBytes()));
		when(resultSet.getBinaryStream("hello")).thenReturn(new ByteArrayInputStream("world".getBytes()));

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals("hello", new String(Streams.copyToByteArray(rs.getBinaryStream(1))));
			assertEquals("world", new String(Streams.copyToByteArray(rs.getBinaryStream("hello"))));
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	void testGetUnicodeStream() throws SQLException, IOException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getUnicodeStream(1)).thenReturn(new ByteArrayInputStream("hello".getBytes()));
		when(resultSet.getUnicodeStream("hello")).thenReturn(new ByteArrayInputStream("world".getBytes()));

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals("hello", new String(Streams.copyToByteArray(rs.getUnicodeStream(1))));
			assertEquals("world", new String(Streams.copyToByteArray(rs.getUnicodeStream("hello"))));
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	void testGetBigDecimal() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getBigDecimal(1)).thenReturn(new BigDecimal(1));
		when(resultSet.getBigDecimal("hello")).thenReturn(new BigDecimal(2));
		when(resultSet.getBigDecimal(1, 1)).thenReturn(new BigDecimal(3));
		when(resultSet.getBigDecimal("hello", 1)).thenReturn(new BigDecimal(4));

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(new BigDecimal(1), rs.getBigDecimal(1));
			assertEquals(new BigDecimal(2), rs.getBigDecimal("hello"));
			assertEquals(new BigDecimal(3), rs.getBigDecimal(1, 1));
			assertEquals(new BigDecimal(4), rs.getBigDecimal("hello", 1));
		}
	}
	
	@Test
	void testGetUrl() throws SQLException, IOException {

		URL url = new URL("http://www.google.be");
		
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getURL(1)).thenReturn(url);
		when(resultSet.getURL("hello")).thenReturn(url);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(url, rs.getURL(1));
			assertEquals(url, rs.getURL("hello"));
		}
	}
	

	@SuppressWarnings("all")
	@Test
	void testGetObject() throws SQLException {

		java.util.Map<String, Class<?>> map = new HashMap<>();

		Date date1 = Date.valueOf(LocalDate.of(2014, 9, 17));
		Date date2 = Date.valueOf(LocalDate.of(2011, 3, 9));

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getObject(1)).thenReturn(date1);
		when(resultSet.getObject("hello")).thenReturn(date2);
		when(resultSet.getObject(1, LocalDate.class)).thenReturn(LocalDate.of(2014, 9, 17));
		when(resultSet.getObject("hello", LocalDate.class)).thenReturn(LocalDate.of(2011, 3, 9));
		when(resultSet.getObject(1, map)).thenReturn(date1);
		when(resultSet.getObject("hello", map)).thenReturn(date2);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(date1, rs.getObject(1));
			assertEquals(date2, rs.getObject("hello"));
			assertEquals(LocalDate.of(2014, 9, 17), rs.getObject(1, LocalDate.class));
			assertEquals(LocalDate.of(2011, 3, 9), rs.getObject("hello", LocalDate.class));
			assertEquals(date1, rs.getObject(1, map));
			assertEquals(date2, rs.getObject("hello", map));
		}
	}

	@Test
	void testGetCharacterStream() throws SQLException, IOException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getCharacterStream(1)).thenReturn(new StringReader("hello"));
		when(resultSet.getCharacterStream("hello")).thenReturn(new StringReader("world"));
		when(resultSet.getNCharacterStream(14)).thenReturn(new StringReader("hello4"));
		when(resultSet.getNCharacterStream("hello4")).thenReturn(new StringReader("world5"));

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals("hello", CharStreams.toString(rs.getCharacterStream(1)));
			assertEquals("world", CharStreams.toString(rs.getCharacterStream("hello")));
			assertEquals("hello4", CharStreams.toString(rs.getNCharacterStream(14)));
			assertEquals("world5", CharStreams.toString(rs.getNCharacterStream("hello4")));
		}
	}
	
	@Test
	void testGetRef() throws SQLException {

		Ref ref = mock(Ref.class);
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getRef(1)).thenReturn(ref);
		when(resultSet.getRef("hello")).thenReturn(ref);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(ref, rs.getRef(1));
			assertEquals(ref, rs.getRef("hello"));
		}
	}
	
	@Test
	void testGetRowId() throws SQLException {

		RowId rowId = mock(RowId.class);
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getRowId(1)).thenReturn(rowId);
		when(resultSet.getRowId("hello")).thenReturn(rowId);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(rowId, rs.getRowId(1));
			assertEquals(rowId, rs.getRowId("hello"));
		}
	}
	
	@Test
	void testGetBlob() throws SQLException {

		Blob blob = mock(Blob.class);
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getBlob(1)).thenReturn(blob);
		when(resultSet.getBlob("hello")).thenReturn(blob);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(blob, rs.getBlob(1));
			assertEquals(blob, rs.getBlob("hello"));
		}
	}
	
	@Test
	void testGetClob() throws SQLException {

		Clob clob = mock(Clob.class);
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getClob(1)).thenReturn(clob);
		when(resultSet.getClob("hello")).thenReturn(clob);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(clob, rs.getClob(1));
			assertEquals(clob, rs.getClob("hello"));
		}
	}
	
	@Test
	void testGetNClob() throws SQLException {

		NClob clob = mock(NClob.class);
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getNClob(1)).thenReturn(clob);
		when(resultSet.getNClob("hello")).thenReturn(clob);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(clob, rs.getNClob(1));
			assertEquals(clob, rs.getNClob("hello"));
		}
	}
	
	@Test
	void testGetSQLXML() throws SQLException {

		SQLXML sqlxml = mock(SQLXML.class);
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getSQLXML(1)).thenReturn(sqlxml);
		when(resultSet.getSQLXML("hello")).thenReturn(sqlxml);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(sqlxml, rs.getSQLXML(1));
			assertEquals(sqlxml, rs.getSQLXML("hello"));
		}
	}
	
	@Test
	void testGetArray() throws SQLException {

		Array array= mock(Array.class);
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getArray(1)).thenReturn(array);
		when(resultSet.getArray("hello")).thenReturn(array);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals(array, rs.getArray(1));
			assertEquals(array, rs.getArray("hello"));
		}
	}

	@Test
	void testGetOthers() throws SQLException {

		ResultSet resultSet = Mockito.mock(ResultSet.class);
		when(resultSet.getWarnings()).thenReturn(new SQLWarning("bla", "gruik"));

		when(resultSet.getCursorName()).thenReturn("CURSOR_NAME");

		when(resultSet.isBeforeFirst()).thenReturn(true);
		when(resultSet.isAfterLast()).thenReturn(true);
		when(resultSet.isFirst()).thenReturn(true);
		when(resultSet.isLast()).thenReturn(true);
		when(resultSet.first()).thenReturn(true);
		when(resultSet.last()).thenReturn(true);
		when(resultSet.previous()).thenReturn(true);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			assertEquals("bla", rs.getWarnings().getMessage());
			assertEquals("gruik", rs.getWarnings().getSQLState());

			// nothing to test
			rs.clearWarnings();
			rs.getMetaData();

			assertEquals("CURSOR_NAME", rs.getCursorName());

			rs.beforeFirst();
			rs.afterLast();

			assertEquals(true, rs.isBeforeFirst());
			assertEquals(true, rs.isAfterLast());
			assertEquals(true, rs.isFirst());
			assertEquals(true, rs.isLast());
			assertEquals(true, rs.first());
			assertEquals(true, rs.last());
			assertEquals(true, rs.previous());
			
			when(resultSet.findColumn("tutu")).thenReturn(5);
			assertEquals(5, rs.findColumn("tutu"));
			
			when(resultSet.getRow()).thenReturn(5);
			assertEquals(5, rs.getRow());
			
			Statement st = mock(Statement.class);
			when(resultSet.getStatement()).thenReturn(st);
			assertEquals(st, rs.getStatement());
		}
	}
	
	@Test
	void testFetchs() throws SQLException {
		
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		
		when(resultSet.absolute(123)).thenReturn(false);
		when(resultSet.relative(456)).thenReturn(false);
		when(resultSet.getFetchDirection()).thenReturn(9999);
		when(resultSet.getFetchSize()).thenReturn(99999);
		when(resultSet.getType()).thenReturn(44);
		when(resultSet.getConcurrency()).thenReturn(55);
		
		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			
			assertEquals(false, rs.absolute(123));
			assertEquals(false, rs.relative(456));
			assertEquals(9999, rs.getFetchDirection());
			assertEquals(99999, rs.getFetchSize());
			
			rs.setFetchDirection(12);
			rs.setFetchSize(77777);
			
			assertEquals(44, rs.getType());
			assertEquals(55, rs.getConcurrency());
			
			
			
		}
		
		
	}

	@Test
	void testUpdate() throws SQLException {

		ResultSet resultSet = mock(ResultSet.class);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {

			rs.updateNull(1);
			rs.updateNull("hello");
			verify(resultSet, times(1)).updateNull(1);
			verify(resultSet, times(1)).updateNull("hello");

			Array array = mock(Array.class);
			rs.updateArray(1, array);
			rs.updateArray("hello", array);
			verify(resultSet, times(1)).updateArray(1, array);
			verify(resultSet, times(1)).updateArray("hello", array);

			InputStream is = new ByteArrayInputStream(new byte[0]);
			rs.updateAsciiStream(1, is);
			rs.updateAsciiStream("hello", is);
			rs.updateAsciiStream(1, is, 20);
			rs.updateAsciiStream(1, is, 21L);
			rs.updateAsciiStream("hello", is, 30);
			rs.updateAsciiStream("hello", is, 31L);
			verify(resultSet, times(1)).updateAsciiStream(1, is);
			verify(resultSet, times(1)).updateAsciiStream("hello", is);
			verify(resultSet, times(1)).updateAsciiStream(1, is, 20);
			verify(resultSet, times(1)).updateAsciiStream(1, is, 21L);
			verify(resultSet, times(1)).updateAsciiStream("hello", is, 30);
			verify(resultSet, times(1)).updateAsciiStream("hello", is, 31L);

			rs.updateBigDecimal(1, new BigDecimal(4));
			rs.updateBigDecimal("hello", new BigDecimal(5));
			verify(resultSet, times(1)).updateBigDecimal(1, new BigDecimal(4));
			verify(resultSet, times(1)).updateBigDecimal("hello", new BigDecimal(5));

			is = new ByteArrayInputStream(new byte[0]);
			rs.updateBinaryStream(2, is);
			rs.updateBinaryStream("hello2", is);
			rs.updateBinaryStream(2, is, 22);
			rs.updateBinaryStream(2, is, 23L);
			rs.updateBinaryStream("hello4", is, 32);
			rs.updateBinaryStream("hello5", is, 33L);
			verify(resultSet, times(1)).updateBinaryStream(2, is);
			verify(resultSet, times(1)).updateBinaryStream("hello2", is);
			verify(resultSet, times(1)).updateBinaryStream(2, is, 22);
			verify(resultSet, times(1)).updateBinaryStream(2, is, 23L);
			verify(resultSet, times(1)).updateBinaryStream("hello4", is, 32);
			verify(resultSet, times(1)).updateBinaryStream("hello5", is, 33L);

			Blob blob = mock(Blob.class);
			rs.updateBlob(1, blob);
			rs.updateBlob("hello", blob);
			rs.updateBlob(1, is);
			rs.updateBlob("hello", is);
			rs.updateBlob(1, is, 10L);
			rs.updateBlob("hello", is, 10L);
			verify(resultSet, times(1)).updateBlob(1, blob);
			verify(resultSet, times(1)).updateBlob("hello", blob);
			verify(resultSet, times(1)).updateBlob(1, is);
			verify(resultSet, times(1)).updateBlob("hello", is);
			verify(resultSet, times(1)).updateBlob(1, is, 10L);
			verify(resultSet, times(1)).updateBlob("hello", is, 10L);

			rs.updateBoolean(1, true);
			rs.updateBoolean("hello", false);
			verify(resultSet, times(1)).updateBoolean(1, true);
			verify(resultSet, times(1)).updateBoolean("hello", false);

			rs.updateByte(1, (byte) 45);
			rs.updateByte("hello", (byte) 46);
			verify(resultSet, times(1)).updateByte(1, (byte) 45);
			verify(resultSet, times(1)).updateByte("hello", (byte) 46);

			rs.updateBytes(1, new byte[0]);
			rs.updateBytes("hello", new byte[] { (byte) 46 });
			verify(resultSet, times(1)).updateBytes(1, new byte[0]);
			verify(resultSet, times(1)).updateBytes("hello", new byte[] { (byte) 46 });

			Reader r = new StringReader("hello world !");
			rs.updateCharacterStream(2, r);
			rs.updateCharacterStream("hello2", r);
			rs.updateCharacterStream(2, r, 22);
			rs.updateCharacterStream(2, r, 23L);
			rs.updateCharacterStream("hello4", r, 32);
			rs.updateCharacterStream("hello5", r, 33L);
			verify(resultSet, times(1)).updateCharacterStream(2, r);
			verify(resultSet, times(1)).updateCharacterStream("hello2", r);
			verify(resultSet, times(1)).updateCharacterStream(2, r, 22);
			verify(resultSet, times(1)).updateCharacterStream(2, r, 23L);
			verify(resultSet, times(1)).updateCharacterStream("hello4", r, 32);
			verify(resultSet, times(1)).updateCharacterStream("hello5", r, 33L);

			Clob clob = mock(Clob.class);
			rs.updateClob(1, clob);
			rs.updateClob("hello", clob);
			rs.updateClob(1, r);
			rs.updateClob("hello", r);
			rs.updateClob(1, r, 10L);
			rs.updateClob("hello", r, 10L);
			verify(resultSet, times(1)).updateClob(1, clob);
			verify(resultSet, times(1)).updateClob("hello", clob);
			verify(resultSet, times(1)).updateClob(1, r);
			verify(resultSet, times(1)).updateClob("hello", r);
			verify(resultSet, times(1)).updateClob(1, r, 10L);
			verify(resultSet, times(1)).updateClob("hello", r, 10L);

			rs.updateDate(1, Date.valueOf(LocalDate.of(2011, 3, 9)));
			rs.updateDate("blable", Date.valueOf(LocalDate.of(2014, 9, 17)));
			verify(resultSet, times(1)).updateDate(1, Date.valueOf(LocalDate.of(2011, 3, 9)));
			verify(resultSet, times(1)).updateDate("blable", Date.valueOf(LocalDate.of(2014, 9, 17)));

			rs.updateDouble(1, 14.25d);
			rs.updateDouble("gruik", 14.26d);
			verify(resultSet, times(1)).updateDouble(1, 14.25d);
			verify(resultSet, times(1)).updateDouble("gruik", 14.26d);

			rs.updateFloat(1, 14.27f);
			rs.updateFloat("gruik2", 14.28f);
			verify(resultSet, times(1)).updateFloat(1, 14.27f);
			verify(resultSet, times(1)).updateFloat("gruik2", 14.28f);

			rs.updateInt(1, 1234);
			rs.updateInt("gruik2", 1458);
			verify(resultSet, times(1)).updateInt(1, 1234);
			verify(resultSet, times(1)).updateInt("gruik2", 1458);

			rs.updateLong(1, 123455L);
			rs.updateLong("gruik2", 145855L);
			verify(resultSet, times(1)).updateLong(1, 123455L);
			verify(resultSet, times(1)).updateLong("gruik2", 145855L);

			rs.updateNCharacterStream(1, r);
			rs.updateNCharacterStream("tipTop", r);
			rs.updateNCharacterStream(1, r, 125L);
			rs.updateNCharacterStream("tipTop", r, 654L);
			verify(resultSet, times(1)).updateNCharacterStream(1, r);
			verify(resultSet, times(1)).updateNCharacterStream("tipTop", r);
			verify(resultSet, times(1)).updateNCharacterStream(1, r, 125L);
			verify(resultSet, times(1)).updateNCharacterStream("tipTop", r, 654L);

			NClob nclob = mock(NClob.class);
			rs.updateNClob(1, nclob);
			rs.updateNClob("hello", nclob);
			rs.updateNClob(1, r);
			rs.updateNClob("hello", r);
			rs.updateNClob(1, r, 10L);
			rs.updateNClob("hello", r, 10L);
			verify(resultSet, times(1)).updateNClob(1, nclob);
			verify(resultSet, times(1)).updateNClob("hello", nclob);
			verify(resultSet, times(1)).updateNClob(1, r);
			verify(resultSet, times(1)).updateNClob("hello", r);
			verify(resultSet, times(1)).updateNClob(1, r, 10L);
			verify(resultSet, times(1)).updateNClob("hello", r, 10L);

			rs.updateNString(1, "hello 123");
			rs.updateNString("hello", "hello 456");
			verify(resultSet, times(1)).updateNString(1, "hello 123");
			verify(resultSet, times(1)).updateNString("hello", "hello 456");

			rs.updateObject(1, "hello 123");
			rs.updateObject("hello", "hello 456");
			rs.updateObject(1, "hello 123", 123456);
			rs.updateObject("hello", "hello 456", 789456);
			assertThrows(SQLFeatureNotSupportedException.class,() -> rs.updateObject(1, "hello 123", JDBCType.VARCHAR));
			assertThrows(SQLFeatureNotSupportedException.class,() -> rs.updateObject("hello", "hello 456", JDBCType.VARCHAR));
			assertThrows(SQLFeatureNotSupportedException.class,() -> rs.updateObject(1, "hello 123", JDBCType.VARCHAR, 77777));
			assertThrows(SQLFeatureNotSupportedException.class,() -> rs.updateObject("hello", "hello 456", JDBCType.VARCHAR, 88888));			verify(resultSet, times(1)).updateObject(1, "hello 123");
			verify(resultSet, times(1)).updateObject(1, "hello 123");
			verify(resultSet, times(1)).updateObject("hello", "hello 456");
			verify(resultSet, times(1)).updateObject(1, "hello 123", 123456);
			verify(resultSet, times(1)).updateObject("hello", "hello 456", 789456);
			
			Ref ref = mock(Ref.class);
			rs.updateRef(1, ref);
			rs.updateRef("hello", ref);
			verify(resultSet, times(1)).updateRef(1, ref);
			verify(resultSet, times(1)).updateRef("hello", ref);
			
			RowId rowId = mock(RowId.class);
			rs.updateRow();
			rs.updateRowId(1, rowId);
			rs.updateRowId("hello", rowId);
			verify(resultSet, times(1)).updateRow();
			verify(resultSet, times(1)).updateRowId(1, rowId);
			verify(resultSet, times(1)).updateRowId("hello", rowId);
			
			rs.updateShort(1, (short)145);
			rs.updateShort("hello", (short)178);
			verify(resultSet, times(1)).updateShort(1, (short)145);
			verify(resultSet, times(1)).updateShort("hello", (short)178);
			
			SQLXML sqlXml = mock(SQLXML.class);
			rs.updateSQLXML(1, sqlXml);
			rs.updateSQLXML("hello", sqlXml);
			verify(resultSet, times(1)).updateSQLXML(1, sqlXml);
			verify(resultSet, times(1)).updateSQLXML("hello", sqlXml);
			
			rs.updateString(1, "gruik gruik");
			rs.updateString("hello", "gruik gruik 123456");
			verify(resultSet, times(1)).updateString(1, "gruik gruik");
			verify(resultSet, times(1)).updateString("hello", "gruik gruik 123456");
			
			rs.updateTime(1, Time.valueOf(LocalTime.of(12, 30, 50)));
			rs.updateTime("hello", Time.valueOf(LocalTime.of(12, 40, 50)));
			verify(resultSet, times(1)).updateTime(1, Time.valueOf(LocalTime.of(12, 30, 50)));
			verify(resultSet, times(1)).updateTime("hello", Time.valueOf(LocalTime.of(12, 40, 50)));
			
			rs.updateTimestamp(1, Timestamp.valueOf(LocalDateTime.of(2014, 9, 17, 0, 0, 0)));
			rs.updateTimestamp("hello", Timestamp.valueOf(LocalDateTime.of(2011, 3, 9, 0, 0, 0)));
			verify(resultSet, times(1)).updateTimestamp(1, Timestamp.valueOf(LocalDateTime.of(2014, 9, 17, 0, 0, 0)));
			verify(resultSet, times(1)).updateTimestamp("hello", Timestamp.valueOf(LocalDateTime.of(2011, 3, 9, 0, 0, 0)));
			
			rs.rowUpdated();
			verify(resultSet, times(1)).rowUpdated();
			rs.cancelRowUpdates();
			verify(resultSet, times(1)).cancelRowUpdates();
			
			rs.rowInserted();
			verify(resultSet, times(1)).rowInserted();
			rs.rowDeleted();
			verify(resultSet, times(1)).rowDeleted();
			
			rs.insertRow();
			verify(resultSet, times(1)).insertRow();
			rs.deleteRow();
			verify(resultSet, times(1)).deleteRow();
			rs.refreshRow();
			verify(resultSet, times(1)).refreshRow();
			rs.moveToInsertRow();
			verify(resultSet, times(1)).moveToInsertRow();
			rs.moveToCurrentRow();
			verify(resultSet, times(1)).moveToCurrentRow();
			
			rs.getHoldability();
			verify(resultSet, times(1)).getHoldability();
			rs.isClosed();
			verify(resultSet, times(1)).isClosed();
			
			rs.next();
			verify(resultSet, times(1)).next();
			rs.wasNull();
			verify(resultSet, times(1)).wasNull();
			
			
		}
	}
	
	@Test
	void testWrap() throws SQLException {
		
		ResultSet resultSet = mock(ResultSet.class);

		try (BraveResultSet rs = new BraveResultSet(resultSet, tracer.span("sample"))) {
			
			rs.isWrapperFor(String.class);
			verify(resultSet, times(1)).isWrapperFor(String.class);
			
			rs.unwrap(String.class);
			verify(resultSet, times(1)).unwrap(String.class);
			
		}
	}
	
}
