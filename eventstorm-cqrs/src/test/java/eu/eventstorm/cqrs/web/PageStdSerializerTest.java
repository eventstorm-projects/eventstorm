package eu.eventstorm.cqrs.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.cqrs.QueryException;
import eu.eventstorm.sql.page.Page;
import eu.eventstorm.sql.page.PageImpl;
import eu.eventstorm.sql.page.Range;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class PageStdSerializerTest {

	@Test
	void test() throws IOException {
		Page<String> page = new PageImpl<>(Stream.of("1","2","3"), 3, new Range(0, 2));
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new PageModule());
		assertEquals("[\"1\",\"2\",\"3\"]", mapper.writeValueAsString(page));
	}
	
	@Test
	void testInvalidWrite() throws IOException {
		Page<Toto> page = new PageImpl<>(Stream.of(new Toto()), 1, new Range(0, 0));
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new PageModule());
		JsonMappingException ex = assertThrows(JsonMappingException.class, () -> mapper.writeValueAsString(page));
		assertEquals(QueryException.class, ex.getCause().getClass());
		assertEquals(QueryException.Type.FAILED_TO_WRITE_PAGE, ((QueryException)ex.getCause()).getType());
	}
	
	private static final class Toto {
		public String getValue() {
			throw new IllegalStateException();
		}
	}
	
}
