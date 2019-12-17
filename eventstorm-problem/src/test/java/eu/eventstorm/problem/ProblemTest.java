package eu.eventstorm.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class ProblemTest {

	private ObjectMapper mapper;
	
	@BeforeEach
	public void beforeEach() {
		mapper = new ObjectMapper();
		mapper.registerModule(new ProblemModule());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void test() throws IOException {
		Problem problem = Problem.builder()
			.withType(URI.create("http://localhost"))
			.withInstance(URI.create("/test/service"))
			.withStatus(400)
			.withTitle("title")
			.withDetail("detail")
			.build();
		
		Map<String,Object> map = this.mapper.readValue(this.mapper.writeValueAsBytes(problem), Map.class);
		
		assertEquals("http://localhost", map.get("type"));
		assertEquals("/test/service", map.get("instance"));
		assertEquals("title", map.get("title"));
		assertEquals("detail", map.get("detail"));
		assertEquals(400, map.get("status"));
		
	}
	
	@Test
	void test2() throws IOException {
		Problem problem = Problem.builder()
			.withType(URI.create("http://localhost"))
			.withInstance(URI.create("/test/service"))
			.withStatus(400)
			.withTitle("title")
			.withDetail("detail")
			.build();
		
		Problem problemResult = this.mapper.readValue(this.mapper.writeValueAsBytes(problem), Problem.class);

		assertEquals(problem.getType(), problemResult.getType());
		assertEquals(problem.getInstance(), problemResult.getInstance());
		assertEquals(problem.getStatus(), problemResult.getStatus());
		assertEquals(problem.getTitle(), problemResult.getTitle());
		assertEquals(problem.getDetail(), problemResult.getDetail());
		assertEquals(problem.getTraceId(), problemResult.getTraceId());
		assertEquals(problem.getTimestamp(), problemResult.getTimestamp());
		
	}
	
}
