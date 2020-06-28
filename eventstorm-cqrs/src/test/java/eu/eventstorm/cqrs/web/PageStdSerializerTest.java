package eu.eventstorm.cqrs.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.sql.page.Page;
import eu.eventstorm.sql.page.PageImpl;
import eu.eventstorm.sql.page.Range;

class PageStdSerializerTest {

	@Test
	void test() throws IOException {
		Page<String> page = new PageImpl<>(Stream.of("1","2","3"), 3, new Range(0, 2));
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new PageModule());
		assertEquals("[\"1\",\"2\",\"3\"]", mapper.writeValueAsString(page));
	}
	
}
