package eu.eventstorm.core.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import com.google.protobuf.TypeRegistry;
import com.jayway.jsonpath.DocumentContext;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.test.RegisteredTest;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.OffsetDateTime;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LoggerInstancePostProcessor.class)
class EventStdSerializerTest {

	@Test
	void ser() throws Exception {
		
		TypeRegistry typeRegistry = TypeRegistry.newBuilder()
				.add(RegisteredTest.getDescriptor())
				.build();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new CoreApiModule(typeRegistry));
		
		OffsetDateTime odt = OffsetDateTime.now();
		
		RegisteredTest test = RegisteredTest.newBuilder()
				.setUser("Jacques")
				.setAge("39")
				.build();
		
		Event event = Event.newBuilder()
				.setStream("test")
				.setStreamId("123-456")
				.setTimestamp(odt.toString())
				.setRevision(1)
				.setData(Any.pack(test,"junit"))
				.build();
		
		String value = mapper.writeValueAsString(event);

		DocumentContext json = parse(value);
		assertEquals("test", json.read("$.stream", String.class));
		assertEquals("123-456", json.read("$.streamId", String.class));
		assertEquals(1, json.read("$.revision", Integer.class));
		assertEquals("junit/eu.eventstorm.core.test.RegisteredTest", json.read("$.data.@type", String.class));
		assertEquals("Jacques", json.read("$.data.user", String.class));
		assertEquals("39", json.read("$.data.age", String.class));
		
	}
}
