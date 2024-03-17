package eu.eventstorm.problem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProblemTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemTest.class);

    private ObjectMapper mapper;

    @BeforeEach
    public void beforeEach() {
        mapper = new ObjectMapper();
        mapper.registerModule(new ProblemModule());
    }

    @Test
    void test() throws IOException {
        Problem problem = Problem.builder()
                .withType(URI.create("http://localhost/test/service"))
                .withInstance(URI.create("http://localhost/error/123456").toASCIIString())
                .withStatus(400)
                .withTitle("title")
                .withDetail("detail")
                .with(ImmutableMap.of("KEY", "VALUE"))
                .build();

        Map<String, Object> map = this.mapper.readValue(this.mapper.writeValueAsBytes(problem), Map.class);

        assertEquals("http://localhost/test/service", map.get("type"));
        assertEquals("http://localhost/error/123456", map.get("instance"));
        assertEquals("title", map.get("title"));
        assertEquals("detail", map.get("detail"));
        assertEquals("VALUE", map.get("KEY"));
        assertEquals(400, map.get("status"));

    }

    @Test
    void test2() throws IOException {

        Problem problem = Problem.builder()
                .withType(URI.create("http://localhost"))
                .withInstance(URI.create("/test/service").toASCIIString())
                .withStatus(400)
                .withTitle("title")
                .withDetail("detail")
                .with("key", "value")
                .with("key2", null)
                .with("key3", Optional.ofNullable(null))
                .with("key4", Optional.ofNullable("hello"))
                .with("key5", 12345)
                .with("key6", ImmutableList.of("A", "B", "C"))
                .with("key7", ImmutableMap.of("A", "B", "C", "D"))
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

    @SuppressWarnings({"unchecked"})
    @Test
    void testMinimum() throws IOException {
        Problem problem = Problem.builder()
                .withType(URI.create("http://localhost"))
                .withStatus(400)
                .withTitle("title")
                .with(ImmutableMap.of("KEY", "VALUE"))
                .build();

        Map<String, Object> map = this.mapper.readValue(this.mapper.writeValueAsBytes(problem), Map.class);

        assertEquals("http://localhost", map.get("type"));
        assertNull(map.get("instance"));
        assertEquals("title", map.get("title"));
        assertNull(map.get("detail"));
        assertEquals("VALUE", map.get("KEY"));
        assertEquals(400, map.get("status"));

    }

    @Test
    void testFullProblem() {
        Problem problem = Problem.builder()
                .withType(URI.create("http://localhost"))
                .withInstance(URI.create("/test/service").toASCIIString())
                .withStatus(400)
                .withTitle("title")
                .withDetail("detail")
                .withTraceId("123456")
                // should sjip following reserved word
                .with("trace_id", "hello")
                .with("null", null)
                .with("optional", java.util.Optional.of("hello"))
                .with("optionalNull", java.util.Optional.ofNullable(null))
                .with("key", "value")
                .build();

        assertEquals("value", problem.getParams().get("key"));
        assertEquals(Optional.empty(), problem.getParams().get("optionalNull"));
        assertEquals(Optional.of("hello"), problem.getParams().get("optional"));
        assertNull(problem.getParams().get("trace_id"));

        assertEquals("123456", problem.getTraceId());
    }


    @Test
    void testToStringInJson() throws Exception {

        Problem problem = Problem.builder()
                .withType(URI.create("http://localhost"))
                .withInstance(URI.create("/test/service").toASCIIString())
                .withStatus(400)
                .withTitle("title")
                .withDetail("detail")
                .withTraceId("123456")
                // should sjip following reserved word
                .with("trace_id", "hello")
                .with("null", null)
                .with("optional", java.util.Optional.of("hello"))
                .with("optionalNull", java.util.Optional.ofNullable(null))
                .with("key", "value")
                .build();

        JSONAssert.assertEquals("{type:\"http://localhost\"}", problem.toString(), false);

    }

    @Test
    void testReactive01() throws IOException {

        ServerHttpRequest request = MockServerHttpRequest.get("http://localhost/context/service/crit?name=value")
                .build();

        Problem problem = Problem.builder()
                .withReactiveRequest(request)
                .withStatus(400)
                .withTitle("title")
                .withDetail("detail")
                .with(ImmutableMap.of("KEY", "VALUE"))
                .build();

        Map<String, Object> map = this.mapper.readValue(this.mapper.writeValueAsBytes(problem), Map.class);

        assertEquals("/context/service/crit", map.get("instance"));
        assertEquals("title", map.get("title"));
        assertEquals("detail", map.get("detail"));
        assertEquals("VALUE", map.get("KEY"));
        assertEquals(400, map.get("status"));

    }

}
