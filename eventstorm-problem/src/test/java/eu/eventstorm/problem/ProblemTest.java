package eu.eventstorm.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import javax.servlet.RequestDispatcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

class ProblemTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProblemTest.class);
	
	private ObjectMapper mapper;
	
	@BeforeEach
	public void beforeEach() {
		mapper = new ObjectMapper();
		mapper.registerModule(new ProblemModule());
	}
	
	@SuppressWarnings({ "unchecked" })
	@Test
	void test() throws IOException {
		Problem problem = Problem.builder()
			.withType(URI.create("http://localhost"))
			.withInstance(URI.create("/test/service"))
			.withStatus(400)
			.withTitle("title")
			.withDetail("detail")
			.with(ImmutableMap.of("KEY","VALUE"))
			.build();
		
		Map<String,Object> map = this.mapper.readValue(this.mapper.writeValueAsBytes(problem), Map.class);
		
		assertEquals("http://localhost", map.get("type"));
		assertEquals("/test/service", map.get("instance"));
		assertEquals("title", map.get("title"));
		assertEquals("detail", map.get("detail"));
		assertEquals("VALUE", map.get("KEY"));
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
		
		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.setScheme("http");
		mockHttpServletRequest.setServerName("localhost");
		mockHttpServletRequest.setServerPort(12345);
		mockHttpServletRequest.setContextPath("/fake");
		
		mockHttpServletRequest.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "http://original/uri");
		
		Problem problem = Problem.builder()
				.with(mockHttpServletRequest)
				.build();
		
		assertEquals("http://localhost:12345/fake", problem.getType().toASCIIString());
		assertEquals("http://original/uri", problem.getInstance().toASCIIString());
		
	}
	
	@Test
	void testProblemWithHttpServletWithoutErrorRequestUri() throws IOException {
		
		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.setScheme("http");
		mockHttpServletRequest.setServerName("localhost");
		mockHttpServletRequest.setServerPort(12345);
		mockHttpServletRequest.setContextPath("/fake");
		
		mockHttpServletRequest.setServletPath("servletPath");
		
		Problem problem = Problem.builder()
				.with(mockHttpServletRequest)
				.build();
		
		assertEquals("http://localhost:12345/fake", problem.getType().toASCIIString());
		assertEquals("servletPath", problem.getInstance().toASCIIString());
		
		mockHttpServletRequest.setPathInfo("/pathInfo");
		problem = Problem.builder()
				.with(mockHttpServletRequest)
				.build();
		
		assertEquals("http://localhost:12345/fake", problem.getType().toASCIIString());
		assertEquals("servletPath/pathInfo", problem.getInstance().toASCIIString());
	
		
		mockHttpServletRequest.setPathInfo("/pathInfo");
		mockHttpServletRequest.setQueryString("?key=value");
		problem = Problem.builder()
				.with(mockHttpServletRequest)
				.build();
		
		assertEquals("http://localhost:12345/fake", problem.getType().toASCIIString());
		assertEquals("servletPath/pathInfo?key=value", problem.getInstance().toASCIIString());
	
	}
	
	@Test
	void testToStringInJson() throws Exception {
		
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
		
		JSONAssert.assertEquals("{type:\"http://localhost\"}", problem.toString(), false);

	}
}
