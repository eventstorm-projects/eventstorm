package eu.eventstorm.sql.tracer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

import brave.Tracer;
import brave.Tracing;
import brave.sampler.Sampler;
import zipkin2.reporter.brave.ZipkinSpanHandler;

class BraveStatementTest {

	private Tracer tracer = Tracing.newBuilder().sampler(Sampler.ALWAYS_SAMPLE).addSpanHandler(ZipkinSpanHandler.create(new LoggingBraveReporter())).build().tracer();

	@Test
	void testExecuteQuery() throws SQLException {
		Statement statement = mock(Statement.class);
		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {

			ResultSet rs = mock(ResultSet.class);
			when(statement.executeQuery("FAKE")).thenReturn(rs);
			assertEquals(rs, ps.executeQuery("FAKE"));

			when(statement.executeUpdate("FAKE")).thenReturn(25);
			assertEquals(25, ps.executeUpdate("FAKE"));
			
			when(statement.execute("FAKE")).thenReturn(true);
			assertEquals(true, ps.execute("FAKE"));
			
			when(statement.execute("FAKE", 1)).thenReturn(false);
			assertEquals(false, ps.execute("FAKE", 1));

			when(statement.execute("FAKE", new int[] { 88 })).thenReturn(false);
			assertEquals(false, ps.execute("FAKE", new int[] { 88 }));

			when(statement.execute("FAKE", new String[] { "TOTO" })).thenReturn(false);
			assertEquals(false, ps.execute("FAKE", new String[] { "TOTO" }));

			when(statement.executeUpdate("FAKE", Statement.NO_GENERATED_KEYS)).thenReturn(12);
			assertEquals(12, ps.executeUpdate("FAKE", Statement.NO_GENERATED_KEYS));

			when(statement.executeUpdate("FAKE", new int[] { 88 })).thenReturn(12);
			assertEquals(12, ps.executeUpdate("FAKE", new int[] { 88 }));

			when(statement.executeUpdate("FAKE", new String[] { "TOTO" })).thenReturn(13);
			assertEquals(13, ps.executeUpdate("FAKE", new String[] { "TOTO" }));

			when(statement.execute("FAKE", 2)).thenThrow(SQLException.class);
			assertThrows(SQLException.class, () -> ps.execute("FAKE", 2));

			when(statement.execute("FAKE", new int[] { 99 })).thenThrow(SQLException.class);
			assertThrows(SQLException.class, () -> ps.execute("FAKE", new int[] { 99 }));

			when(statement.execute("FAKE", new String[] { "TOTO_EXCEPTION" })).thenThrow(SQLException.class);
			assertThrows(SQLException.class, () -> ps.execute("FAKE", new String[] { "TOTO_EXCEPTION" }));

			when(statement.executeQuery("FAKE")).thenThrow(SQLException.class);
			assertThrows(SQLException.class, () -> ps.executeQuery("FAKE"));

			when(statement.executeUpdate("FAKE")).thenThrow(SQLException.class);
			assertThrows(SQLException.class, () -> ps.executeUpdate("FAKE"));
			
			when(statement.getGeneratedKeys()).thenReturn(rs);
			assertEquals(rs, ps.getGeneratedKeys());

		}
	}

	@Test
	void testMaxFieldSize() throws SQLException {
		Statement statement = mock(Statement.class);
		when(statement.getMaxFieldSize()).thenReturn(145);
		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {
			assertEquals(145, ps.getMaxFieldSize());

			ps.setMaxFieldSize(888);
			verify(statement, times(1)).setMaxFieldSize(888);
		}
	}

	@Test
	void testMaxRows() throws SQLException {
		Statement statement = mock(Statement.class);
		when(statement.getMaxRows()).thenReturn(145);
		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {
			assertEquals(145, ps.getMaxRows());

			ps.setMaxRows(888);
			verify(statement, times(1)).setMaxRows(888);
		}
	}

	@Test
	void testQueryTimeout() throws SQLException {
		Statement statement = mock(Statement.class);
		when(statement.getQueryTimeout()).thenReturn(7777);
		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {
			assertEquals(7777, ps.getQueryTimeout());

			ps.setQueryTimeout(8888);
			verify(statement, times(1)).setQueryTimeout(8888);
		}
	}

	@Test
	void testWarnings() throws SQLException {
		Statement statement = mock(Statement.class);

		SQLWarning warning = mock(SQLWarning.class);
		when(statement.getWarnings()).thenReturn(warning);

		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {
			assertEquals(warning, ps.getWarnings());

			ps.clearWarnings();
			verify(statement, times(1)).clearWarnings();
		}
	}

	@Test
	void testFetch() throws SQLException {
		Statement statement = mock(Statement.class);

		when(statement.getFetchDirection()).thenReturn(8745);
		when(statement.getFetchSize()).thenReturn(9874);

		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {
			assertEquals(8745, ps.getFetchDirection());
			assertEquals(9874, ps.getFetchSize());

			ps.setFetchDirection(1452);
			ps.setFetchSize(1453);
			verify(statement, times(1)).setFetchDirection(1452);
			verify(statement, times(1)).setFetchSize(1453);
		}
	}

	@Test
	void testResultSet() throws SQLException {

		Statement statement = mock(Statement.class);
		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {

			when(statement.getResultSetConcurrency()).thenReturn(1234);
			assertEquals(1234, ps.getResultSetConcurrency());

			when(statement.getResultSetType()).thenReturn(5678);
			assertEquals(5678, ps.getResultSetType());

			when(statement.getResultSetHoldability()).thenReturn(22);
			assertEquals(22, ps.getResultSetHoldability());

		}
	}

	@Test
	void testPoolable() throws SQLException {

		Statement statement = mock(Statement.class);

		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {

			when(statement.isPoolable()).thenReturn(false);
			assertEquals(false, ps.isPoolable());

			ps.setPoolable(false);
			verify(statement, times(1)).setPoolable(false);
		}
	}

	@Test
	void testCompletion() throws SQLException {

		Statement statement = mock(Statement.class);

		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {

			ps.closeOnCompletion();
			verify(statement, times(1)).closeOnCompletion();

			when(statement.isCloseOnCompletion()).thenReturn(false);
			assertEquals(false, ps.isCloseOnCompletion());
		}
	}

	@Test
	void testWrap() throws SQLException {

		Statement statement = mock(Statement.class);

		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {

			when(statement.unwrap(String.class)).thenReturn("hello");
			assertEquals("hello", ps.unwrap(String.class));

			when(statement.isWrapperFor(Integer.class)).thenReturn(false);
			assertEquals(false, ps.isWrapperFor(Integer.class));
		}
	}

	@Test
	void testMisc() throws SQLException {

		Statement statement = mock(Statement.class);

		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {
			ps.setEscapeProcessing(false);
			verify(statement, times(1)).setEscapeProcessing(false);

			ps.cancel();
			verify(statement, times(1)).cancel();

			doThrow(SQLException.class).when(statement).cancel();
			assertThrows(SQLException.class, () -> ps.cancel());

			ps.setCursorName("CURSOR");
			verify(statement, times(1)).setCursorName("CURSOR");

			ps.getConnection();
			verify(statement, times(1)).getConnection();

			when(statement.getUpdateCount()).thenReturn(8);
			assertEquals(8, ps.getUpdateCount());

			when(statement.getMoreResults()).thenReturn(false);
			assertEquals(false, ps.getMoreResults());

			when(statement.getMoreResults(1)).thenReturn(true);
			assertEquals(true, ps.getMoreResults(1));

			ResultSet rs = mock(ResultSet.class);
			when(statement.getResultSet()).thenReturn(rs);
			assertEquals(rs, ps.getResultSet());
		}

	}

	@Test
	void testBatchs() throws SQLException {

		Statement statement = mock(Statement.class);

		try (BraveStatement ps = new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer))) {

			ps.addBatch("TEST");
			verify(statement, times(1)).addBatch("TEST");

			ps.clearBatch();
			verify(statement, times(1)).clearBatch();

			when(statement.executeBatch()).thenReturn(new int[] { 1, 2, 3 });
			assertArrayEquals(new int[] { 1, 2, 3 }, ps.executeBatch());

		}
	}

	@Test
	void testClose() throws SQLException {
		
		Statement statement = mock(Statement.class);
		doThrow(SQLException.class).when(statement).close();
		
		assertThrows(SQLException.class, () -> new BraveStatement(statement, (BraveTracer) TransactionTracers.brave(tracer)).close());

		Statement st = mock(Statement.class);

		try (BraveStatement ps = new BraveStatement(st, (BraveTracer) TransactionTracers.brave(tracer))) {
		
			when(st.isClosed()).thenReturn(false);
			assertEquals(false, ps.isClosed());
		}
			
	}
}
