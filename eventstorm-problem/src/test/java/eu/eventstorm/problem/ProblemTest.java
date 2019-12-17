package eu.eventstorm.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

class ProblemTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProblemTest.class);
	
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
			.with("key", "value")
			.with("key2", null)
			.with("key3", Optional.ofNullable(null))
			.with("key4", Optional.ofNullable("hello"))
			.build();

		byte[] content = this.mapper.writeValueAsBytes(problem);
		
		LOGGER.info("{}", new String(content));
		
		Problem problemResult = this.mapper.readValue(content, Problem.class);

		assertEquals(problem.getType(), problemResult.getType());
		assertEquals(problem.getInstance(), problemResult.getInstance());
		assertEquals(problem.getStatus(), problemResult.getStatus());
		assertEquals(problem.getTitle(), problemResult.getTitle());
		assertEquals(problem.getDetail(), problemResult.getDetail());
		assertEquals(problem.getTraceId(), problemResult.getTraceId());
		assertEquals(problem.getTimestamp(), problemResult.getTimestamp());
		
	}
	
	@Test
	void testFullProblem() throws IOException {
		Problem problem = Problem.builder()
			.withType(URI.create("http://localhost"))
			.withInstance(URI.create("/test/service"))
			.withStatus(400)
			.withTitle("title")
			.withDetail("detail")
			.withTraceId("123456")
			// should sjip following reserved word
			.with("trace_id","hello")
			.with("null", null)
			.with("optional", java.util.Optional.of("hello"))
			.with("optionalNull", java.util.Optional.ofNullable(null))
			.with("key","value")
			.build();
		
		assertEquals("value", problem.getParams().get("key"));
		assertEquals(Optional.empty(), problem.getParams().get("optionalNull"));
		assertEquals(Optional.of("hello"), problem.getParams().get("optional"));
		assertNull(problem.getParams().get("trace_id"));
		
		assertEquals("123456", problem.getTraceId());
	}
	
	@Test
	void testProblemWithHttpServlet() throws IOException {
		
		
	}
	
}
