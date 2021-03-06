package eu.eventstorm.sql.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import eu.eventstorm.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class ResultSetMappersTest {

	@Test
	void singleLongTest() throws SQLException {

		Dialect dialect = mock(Dialect.class);
		ResultSet rs = mock(ResultSet.class);

		when(rs.getLong(1)).thenReturn(123456L);
		when(rs.next()).thenReturn(false);
		assertEquals(123456L, ResultSetMappers.LONG.map(dialect, rs));

		reset(rs);
		when(rs.getLong(1)).thenReturn(123456L);
		when(rs.next()).thenReturn(true);
		ResultSetMappers.ResultSetMapperException ex = assertThrows(ResultSetMappers.ResultSetMapperException.class, () -> ResultSetMappers.SINGLE_LONG.map(dialect, rs));
		
		assertEquals(ResultSetMappers.ResultSetMapperException.Type.MORE_THAN_ONE_RESULT, ex.getType());
	}
	
	@Test
	void singleSingleIntegerTest() throws SQLException {

		Dialect dialect = mock(Dialect.class);
		ResultSet rs = mock(ResultSet.class);

		when(rs.getInt(1)).thenReturn(123456);
		when(rs.next()).thenReturn(false);
		assertEquals(123456, ResultSetMappers.SINGLE_INTEGER.map(dialect, rs));

		reset(rs);
		when(rs.getInt(1)).thenReturn(123456);
		when(rs.next()).thenReturn(true);
		ResultSetMappers.ResultSetMapperException ex = assertThrows(ResultSetMappers.ResultSetMapperException.class, () -> ResultSetMappers.SINGLE_INTEGER.map(dialect, rs));
		
		assertEquals(ResultSetMappers.ResultSetMapperException.Type.MORE_THAN_ONE_RESULT, ex.getType());
	}
	
	@Test
	void singleIntegerTest() throws SQLException {

		Dialect dialect = mock(Dialect.class);
		ResultSet rs = mock(ResultSet.class);

		when(rs.getInt(1)).thenReturn(123456);
		when(rs.next()).thenReturn(false);
		assertEquals(123456, ResultSetMappers.INTEGER.map(dialect, rs));
		assertEquals(123456, ResultSetMappers.INTEGER_NULLABLE.map(dialect, rs));

		when(rs.wasNull()).thenReturn(true);
		assertEquals(null, ResultSetMappers.INTEGER_NULLABLE.map(dialect, rs));

		reset(rs);
		when(rs.getInt(1)).thenReturn(123456);
		when(rs.next()).thenReturn(true);
		assertEquals(123456, ResultSetMappers.INTEGER.map(dialect, rs));
	}

	@Test
	void shortTest() throws SQLException {

		Dialect dialect = mock(Dialect.class);
		ResultSet rs = mock(ResultSet.class);

		when(rs.getShort(1)).thenReturn((short) 1234);
		when(rs.next()).thenReturn(false);
		assertEquals((short)1234, ResultSetMappers.SHORT.map(dialect, rs));
		assertEquals((short)1234, ResultSetMappers.SHORT_NULLABLE.map(dialect, rs));

		when(rs.wasNull()).thenReturn(true);
		assertEquals(null, ResultSetMappers.SHORT_NULLABLE.map(dialect, rs));
	}

	@Test
	void byteTest() throws SQLException {

		Dialect dialect = mock(Dialect.class);
		ResultSet rs = mock(ResultSet.class);

		when(rs.getByte(1)).thenReturn((byte) 123);
		when(rs.next()).thenReturn(false);
		assertEquals((byte) 123, ResultSetMappers.BYTE.map(dialect, rs));
		assertEquals((byte) 123, ResultSetMappers.BYTE_NULLABLE.map(dialect, rs));

		when(rs.wasNull()).thenReturn(true);
		assertEquals(null, ResultSetMappers.BYTE_NULLABLE.map(dialect, rs));

	}
	
	@Test
	void stringTest() throws SQLException {

		Dialect dialect = mock(Dialect.class);
		ResultSet rs = mock(ResultSet.class);

		when(rs.getString(1)).thenReturn("hello world");

		assertEquals("hello world", ResultSetMappers.STRING.map(dialect, rs));
	}

}
