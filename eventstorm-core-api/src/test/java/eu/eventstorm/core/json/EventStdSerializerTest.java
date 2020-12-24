package eu.eventstorm.core.json;

import java.time.OffsetDateTime;

import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import com.google.protobuf.TypeRegistry;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.test.RegisteredTest;
import org.junit.jupiter.api.extension.ExtendWith;

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
		
		System.out.println(value);
		
	}
}
