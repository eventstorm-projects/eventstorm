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
	void singleIntegerTest() throws SQLException {

		Dialect dialect = mock(Dialect.class);
		ResultSet rs = mock(ResultSet.class);

		when(rs.getInt(1)).thenReturn(123456);
		when(rs.next()).thenReturn(false);
		assertEquals(123456, ResultSetMappers.INTEGER.map(dialect, rs));

		reset(rs);
		when(rs.getInt(1)).thenReturn(123456);
		when(rs.next()).thenReturn(true);
		ResultSetMappers.ResultSetMapperException ex = assertThrows(ResultSetMappers.ResultSetMapperException.class, () -> ResultSetMappers.INTEGER.map(dialect, rs));
		
		assertEquals(ResultSetMappers.ResultSetMapperException.Type.MORE_THAN_ONE_RESULT, ex.getType());
	}
	
	@Test
	void stringTest() throws SQLException {

		Dialect dialect = mock(Dialect.class);
		ResultSet rs = mock(ResultSet.class);

		when(rs.getString(1)).thenReturn("hello world");

		assertEquals("hello world", ResultSetMappers.STRING.map(dialect, rs));
	}

}
