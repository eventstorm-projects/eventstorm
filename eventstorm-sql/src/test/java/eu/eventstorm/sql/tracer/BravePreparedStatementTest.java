package eu.eventstorm.sql.tracer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import brave.Tracer;
import brave.Tracing;
import brave.sampler.Sampler;
import eu.eventstorm.sql.tracer.BravePreparedStatement;
import eu.eventstorm.sql.tracer.BraveTracer;
import eu.eventstorm.sql.tracer.LoggingBraveReporter;
import eu.eventstorm.sql.tracer.TransactionTracers;

class BravePreparedStatementTest {

	private Tracer tracer = Tracing.newBuilder().sampler(Sampler.ALWAYS_SAMPLE).spanReporter(new LoggingBraveReporter()).build().tracer();

	@Test
	void testExecute() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement, (BraveTracer)TransactionTracers.brave(tracer))) {
			
			ps.execute();
			verify(preparedStatement, times(1)).execute();
			
			ps.executeUpdate();
			verify(preparedStatement, times(1)).executeUpdate();
		
			ps.executeQuery();
			verify(preparedStatement, times(1)).executeQuery();
			
			ps.addBatch();
			verify(preparedStatement, times(1)).addBatch();

		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	void testSetter() throws SQLException, IOException {

		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement, (BraveTracer)TransactionTracers.brave(tracer))) {

			Array array = mock(Array.class);
			ps.setArray(1, array);
			verify(preparedStatement, times(1)).setArray(1, array);

			InputStream is = mock(InputStream.class);
			ps.setAsciiStream(2, is);
			verify(preparedStatement, times(1)).setAsciiStream(2, is);
			ps.setAsciiStream(2, is, 48);
			verify(preparedStatement, times(1)).setAsciiStream(2, is, 48);
			ps.setAsciiStream(2, is, 489L);
			verify(preparedStatement, times(1)).setAsciiStream(2, is, 489L);

			ps.setBigDecimal(4, BigDecimal.valueOf(45L));
			verify(preparedStatement, times(1)).setBigDecimal(4, BigDecimal.valueOf(45L));

			ps.setBinaryStream(3, is);
			verify(preparedStatement, times(1)).setBinaryStream(3, is);
			ps.setBinaryStream(3, is, 48);
			verify(preparedStatement, times(1)).setBinaryStream(3, is, 48);
			ps.setBinaryStream(3, is, 489L);
			verify(preparedStatement, times(1)).setBinaryStream(3, is, 489L);

			ps.setUnicodeStream(3, is, 48);
			verify(preparedStatement, times(1)).setUnicodeStream(3, is, 48);

			Blob blob = mock(Blob.class);
			ps.setBlob(88, blob);
			verify(preparedStatement, times(1)).setBlob(88, blob);
			ps.setBlob(88, is);
			verify(preparedStatement, times(1)).setBlob(88, is);
			ps.setBlob(88, is, 666L);
			verify(preparedStatement, times(1)).setBlob(88, is, 666L);

			ps.setBoolean(88, false);
			verify(preparedStatement, times(1)).setBoolean(88, false);

			ps.setByte(88, (byte) 55);
			verify(preparedStatement, times(1)).setByte(88, (byte) 55);

			ps.setBytes(99, new byte[] { (byte) 55 });
			verify(preparedStatement, times(1)).setBytes(99, new byte[] { (byte) 55 });

			Reader reader = new StringReader("gello");
			ps.setCharacterStream(44, reader);
			verify(preparedStatement, times(1)).setCharacterStream(44, reader);
			ps.setCharacterStream(44, reader, 33);
			verify(preparedStatement, times(1)).setCharacterStream(44, reader, 33);
			ps.setCharacterStream(44, reader, 333L);
			verify(preparedStatement, times(1)).setCharacterStream(44, reader, 333L);

			ps.setNCharacterStream(44, reader);
			verify(preparedStatement, times(1)).setNCharacterStream(44, reader);
			ps.setNCharacterStream(44, reader, 33);
			verify(preparedStatement, times(1)).setNCharacterStream(44, reader, 33);

			Clob clob = mock(Clob.class);
			ps.setClob(88, clob);
			verify(preparedStatement, times(1)).setClob(88, clob);
			ps.setClob(88, reader);
			verify(preparedStatement, times(1)).setClob(88, reader);
			ps.setClob(88, reader, 666L);
			verify(preparedStatement, times(1)).setClob(88, reader, 666L);

			NClob nclob = mock(NClob.class);
			ps.setNClob(99, nclob);
			verify(preparedStatement, times(1)).setNClob(99, nclob);
			ps.setNClob(99, reader);
			verify(preparedStatement, times(1)).setNClob(99, reader);
			ps.setNClob(99, reader, 666L);
			verify(preparedStatement, times(1)).setNClob(99, reader, 666L);

			Date date1 = Date.valueOf(LocalDate.of(2014, 9, 17));
			Calendar calendar = Calendar.getInstance();
			ps.setDate(17, date1);
			verify(preparedStatement, times(1)).setDate(17, date1);
			ps.setDate(17, date1, calendar);
			verify(preparedStatement, times(1)).setDate(17, date1, calendar);

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ps.setTimestamp(17, timestamp, calendar);
			verify(preparedStatement, times(1)).setTimestamp(17, timestamp, calendar);
			ps.setTimestamp(16, timestamp);
			verify(preparedStatement, times(1)).setTimestamp(16, timestamp);
			
			Time time = new Time(System.currentTimeMillis());
			ps.setTime(14, time, calendar);
			verify(preparedStatement, times(1)).setTime(14, time, calendar);
			ps.setTime(13, time);
			verify(preparedStatement, times(1)).setTime(13, time);
			
			
			ps.setFloat(44, 12.23F);
			verify(preparedStatement, times(1)).setFloat(44, 12.23F);

			ps.setDouble(44, 12.23D);
			verify(preparedStatement, times(1)).setDouble(44, 12.23D);

			ps.setInt(45, 46);
			verify(preparedStatement, times(1)).setInt(45, 46);

			ps.setLong(45, 46L);
			verify(preparedStatement, times(1)).setLong(45, 46L);

			ps.setShort(45, (short) 46);
			verify(preparedStatement, times(1)).setShort(45, (short) 46);

			ps.setString(45, "46");
			verify(preparedStatement, times(1)).setString(45, "46");

			SQLXML xml = mock(SQLXML.class);
			ps.setSQLXML(88, xml);
			verify(preparedStatement, times(1)).setSQLXML(88, xml);

			ps.setNString(45, "46");
			verify(preparedStatement, times(1)).setNString(45, "46");

			ps.setObject(45, "46");
			verify(preparedStatement, times(1)).setObject(45, "46");
			ps.setObject(45, 4444444, JDBCType.BIGINT);
			verify(preparedStatement, times(1)).setObject(45, 4444444, JDBCType.BIGINT);
			ps.setObject(45, 44444449, 4);
			verify(preparedStatement, times(1)).setObject(45, 44444449, 4);
			ps.setObject(45, 74444444, JDBCType.BIGINT, 4);
			verify(preparedStatement, times(1)).setObject(45, 74444444, JDBCType.BIGINT, 4);
			ps.setObject(45, 744444449, 4, 5);
			verify(preparedStatement, times(1)).setObject(45, 744444449, 4, 5);

			ps.setNull(45, 46);
			verify(preparedStatement, times(1)).setNull(45, 46);
			ps.setNull(45, 467, "fake");
			verify(preparedStatement, times(1)).setNull(45, 467, "fake");

			Ref ref = mock(Ref.class);
			ps.setRef(74, ref);
			verify(preparedStatement, times(1)).setRef(74, ref);
			
			RowId rowId = mock(RowId.class);
			ps.setRowId(75, rowId);
			verify(preparedStatement, times(1)).setRowId(75, rowId);
			
			URL url = new URL("http://www.google.be");
			ps.setURL(75, url);
			verify(preparedStatement, times(1)).setURL(75, url);
		}
	}
	
	
	@Test
	void testMisc() throws SQLException {
		
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement, (BraveTracer)TransactionTracers.brave(tracer))) {
		
			ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);
			when(preparedStatement.getMetaData()).thenReturn(resultSetMetaData);
			assertEquals(resultSetMetaData, ps.getMetaData());
			
			ParameterMetaData parameterMetaData = mock(ParameterMetaData.class);
			when(preparedStatement.getParameterMetaData()).thenReturn(parameterMetaData);
			assertEquals(parameterMetaData, ps.getParameterMetaData());
			
			ps.clearParameters();
			verify(preparedStatement, times(1)).clearParameters();
		}
	}
		
	
	/*
	
	@Test
	void testExecuteLargeUpdate() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement, (BraveTracer)TransactionTracers.brave(tracer))) {


			when(preparedStatement.executeLargeUpdate()).thenReturn(1234L);
			assertEquals(1234L, ps.executeLargeUpdate());

			when(preparedStatement.executeLargeUpdate("FAKE")).thenReturn(5678L);
			assertEquals(5678L, ps.executeLargeUpdate("FAKE"));

			when(preparedStatement.executeLargeUpdate("FAKE2", 1)).thenReturn(56789L);
			assertEquals(56789L, ps.executeLargeUpdate("FAKE2", 1));

			when(preparedStatement.executeLargeUpdate("FAKE2", new int[] { 1, 2 })).thenReturn(567890L);
			assertEquals(567890L, ps.executeLargeUpdate("FAKE2", new int[] { 1, 2 }));

			when(preparedStatement.executeLargeUpdate("FAKE2", new String[] { "test1", "test2" })).thenReturn(56789077L);
			assertEquals(56789077L, ps.executeLargeUpdate("FAKE2", new String[] { "test1", "test2" }));

		}
	}
	*/
/*
	@Test
	void testExecuteUpdate() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {

			when(preparedStatement.executeUpdate()).thenReturn(1234);
			assertEquals(1234, ps.executeUpdate());

			when(preparedStatement.executeUpdate("FAKE")).thenReturn(5678);
			assertEquals(5678, ps.executeUpdate("FAKE"));

			when(preparedStatement.executeUpdate("FAKE2", 1)).thenReturn(56789);
			assertEquals(56789, ps.executeUpdate("FAKE2", 1));

			when(preparedStatement.executeUpdate("FAKE2", new int[] { 1, 2 })).thenReturn(567890);
			assertEquals(567890, ps.executeUpdate("FAKE2", new int[] { 1, 2 }));

			when(preparedStatement.executeUpdate("FAKE2", new String[] { "test1", "test2" })).thenReturn(56789077);
			assertEquals(56789077, ps.executeUpdate("FAKE2", new String[] { "test1", "test2" }));

		}
	}

	@Test
	void testExecuteLargeUpdate() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {

			when(preparedStatement.executeLargeUpdate()).thenReturn(1234L);
			assertEquals(1234L, ps.executeLargeUpdate());

			when(preparedStatement.executeLargeUpdate("FAKE")).thenReturn(5678L);
			assertEquals(5678L, ps.executeLargeUpdate("FAKE"));

			when(preparedStatement.executeLargeUpdate("FAKE2", 1)).thenReturn(56789L);
			assertEquals(56789L, ps.executeLargeUpdate("FAKE2", 1));

			when(preparedStatement.executeLargeUpdate("FAKE2", new int[] { 1, 2 })).thenReturn(567890L);
			assertEquals(567890L, ps.executeLargeUpdate("FAKE2", new int[] { 1, 2 }));

			when(preparedStatement.executeLargeUpdate("FAKE2", new String[] { "test1", "test2" })).thenReturn(56789077L);
			assertEquals(56789077L, ps.executeLargeUpdate("FAKE2", new String[] { "test1", "test2" }));

		}
	}

	@Test
	void testMaxFieldSize() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		when(preparedStatement.getMaxFieldSize()).thenReturn(145);
		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {
			assertEquals(145, ps.getMaxFieldSize());

			ps.setMaxFieldSize(888);
			verify(preparedStatement, times(1)).setMaxFieldSize(888);
		}
	}

	@Test
	void testMaxRows() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		when(preparedStatement.getMaxRows()).thenReturn(145);
		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {
			assertEquals(145, ps.getMaxRows());

			ps.setMaxRows(888);
			verify(preparedStatement, times(1)).setMaxRows(888);
		}
	}

	@Test
	void testQueryTimeout() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		when(preparedStatement.getQueryTimeout()).thenReturn(7777);
		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {
			assertEquals(7777, ps.getQueryTimeout());

			ps.setQueryTimeout(8888);
			verify(preparedStatement, times(1)).setQueryTimeout(8888);
		}
	}

	@Test
	void testWarnings() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		SQLWarning warning = mock(SQLWarning.class);
		when(preparedStatement.getWarnings()).thenReturn(warning);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {

			assertEquals(warning, ps.getWarnings());

			ps.clearWarnings();
			verify(preparedStatement, times(1)).clearWarnings();
		}
	}

	@Test
	void testFetch() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		when(preparedStatement.getFetchDirection()).thenReturn(8745);
		when(preparedStatement.getFetchSize()).thenReturn(9874);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {

			assertEquals(8745, ps.getFetchDirection());
			assertEquals(9874, ps.getFetchSize());

			ps.setFetchDirection(1452);
			ps.setFetchSize(1453);
			verify(preparedStatement, times(1)).setFetchDirection(1452);
			verify(preparedStatement, times(1)).setFetchSize(1453);
		}
	}

	@Test
	void testResultSet() throws SQLException {

		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {

			when(preparedStatement.getResultSetConcurrency()).thenReturn(1234);
			assertEquals(1234, ps.getResultSetConcurrency());

			when(preparedStatement.getResultSetType()).thenReturn(5678);
			assertEquals(5678, ps.getResultSetType());

			when(preparedStatement.getResultSetHoldability()).thenReturn(22);
			assertEquals(22, ps.getResultSetHoldability());

		}
	}

	@Test
	void testPoolable() throws SQLException {

		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {

			when(preparedStatement.isPoolable()).thenReturn(false);
			assertEquals(false, ps.isPoolable());

			ps.setPoolable(false);
			verify(preparedStatement, times(1)).setPoolable(false);
		}
	}

	@Test
	void testBatchs() throws SQLException {

		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {

			ps.addBatch("TEST");
			verify(preparedStatement, times(1)).addBatch("TEST");

			ps.clearBatch();
			verify(preparedStatement, times(1)).clearBatch();

			when(preparedStatement.executeBatch()).thenReturn(new int[] { 1, 2, 3 });
			assertArrayEquals(new int[] { 1, 2, 3 }, ps.executeBatch());
			
			ps.addBatch();
			verify(preparedStatement, times(1)).addBatch();
		}
	}

	

	@Test
	void testOthers() throws SQLException {
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		try (BravePreparedStatement ps = new BravePreparedStatement(preparedStatement,
		        new ZipkinRepositoryEvent("SELECT TEST", tracer, RepositoryEventType.SELECT, "SELECT ..."))) {

			ps.setEscapeProcessing(false);
			verify(preparedStatement, times(1)).setEscapeProcessing(false);

			ps.cancel();
			verify(preparedStatement, times(1)).cancel();

			ps.setCursorName("CURSOR");
			verify(preparedStatement, times(1)).setCursorName("CURSOR");

			ps.getConnection();
			verify(preparedStatement, times(1)).getConnection();

			ResultSet resultSet = mock(ResultSet.class);
			when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
			assertEquals(resultSet, ps.getGeneratedKeys());
			when(preparedStatement.getResultSet()).thenReturn(resultSet);
			assertEquals(resultSet, ps.getResultSet());
			
			ps.closeOnCompletion();
			verify(preparedStatement, times(1)).closeOnCompletion();

			when(preparedStatement.isCloseOnCompletion()).thenReturn(false);
			assertEquals(false, ps.isCloseOnCompletion());

			when(preparedStatement.unwrap(String.class)).thenReturn("hello");
			assertEquals("hello", ps.unwrap(String.class));

			when(preparedStatement.isWrapperFor(Integer.class)).thenReturn(false);
			assertEquals(false, ps.isWrapperFor(Integer.class));
			
			when(preparedStatement.getUpdateCount()).thenReturn(8);
			assertEquals(8, ps.getUpdateCount());
			
			when(preparedStatement.getMoreResults()).thenReturn(false);
			assertEquals(false, ps.getMoreResults());
			
			when(preparedStatement.getMoreResults(1)).thenReturn(true);
			assertEquals(true, ps.getMoreResults(1));
			
			when(preparedStatement.isClosed()).thenReturn(false);
			assertEquals(false, ps.isClosed());
			
			ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);
			when(preparedStatement.getMetaData()).thenReturn(resultSetMetaData);
			assertEquals(resultSetMetaData, ps.getMetaData());
			
			ParameterMetaData parameterMetaData = mock(ParameterMetaData.class);
			when(preparedStatement.getParameterMetaData()).thenReturn(parameterMetaData);
			assertEquals(parameterMetaData, ps.getParameterMetaData());
			
			ps.clearParameters();
			verify(preparedStatement, times(1)).clearParameters();

		}
	}
	*/
}
