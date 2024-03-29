package eu.eventstorm.cqrs.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import eu.eventstorm.page.PageImpl;
import eu.eventstorm.page.Range;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class PageResponseEntityTest {

	@Test
	void test() {
		
		PageResponseEntity<String> responseEntity = new PageResponseEntity<>(new PageImpl<>(Stream.of("1","2","3"), 3, new Range(0, 2)));
		
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("0-2/3", responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE));
		
		
		responseEntity = new PageResponseEntity<>(new PageImpl<>(Stream.of("1","2","3","4","5","6"), 6, new Range(3, 5)));
		assertEquals(HttpStatus.PARTIAL_CONTENT, responseEntity.getStatusCode());
		assertEquals("3-5/6", responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE));
		
		
		responseEntity = new PageResponseEntity<>(new PageImpl<>(Stream.of("1","2","3","4","5"), 10, new Range(0, 4)));
		assertEquals(HttpStatus.PARTIAL_CONTENT, responseEntity.getStatusCode());
		assertEquals("0-4/10", responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE));

		responseEntity = new PageResponseEntity<>(new PageImpl<>(Stream.of("1","2","3","4","5"), 10, new Range(0, 9)));
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("0-9/10", responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE));

		responseEntity = new PageResponseEntity<>(new PageImpl<>(Stream.of(), 0, new Range(0, 10)));
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("0-0/0", responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE));
	}

}
