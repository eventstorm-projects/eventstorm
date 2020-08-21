package eu.eventstorm.cloudevents;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.TypeRegistry;

import eu.eventstorm.cloudevents.json.jackson.CloudEventsModule;

class CloudEventsModuleTest {

	private ObjectMapper objectMapper;
	
	@BeforeEach
	void beforeEach() {
		TypeRegistry typeRegistry = TypeRegistry.newBuilder()
				.add(SimpleMessage.getDescriptor())
				.build();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new CloudEventsModule(typeRegistry));
	}
	
	@Test
	void testSerDeser() throws IOException, Exception {
		
		CloudEvent event = new CloudEventBuilder()
				.withAggregateId("1")
				.withAggregateType("test")
				.withTimestamp("2011-03-09T18:36:30+02:00")
				.withVersion(1)
				.withPayload(SimpleMessage.newBuilder().setName("Jacques").build())
				.build();
		
		StringWriter writer = new StringWriter();
		objectMapper.writeValue(writer, event);

		JSONAssert.assertEquals("{id:\"1\", datacontenttype:\"application/json\"}", writer.toString(), false);
		JSONAssert.assertEquals("{data:{\"name\":\"Jacques\"}}", writer.toString(), false);
		
		objectMapper.readValue(writer.toString(), CloudEvent.class);
	}
	
	@Test
	void testSerDeserAny() throws IOException, Exception {
		
		CloudEvent event = new CloudEventBuilder()
				.withAggregateId("1")
				.withAggregateType("test")
				.withTimestamp("2011-03-09T18:36:30+02:00")
				.withVersion(1)
				.withPayload(Any.pack(SimpleMessage.newBuilder().setName("Jacques").build()))
				.build();
		
		StringWriter writer = new StringWriter();
		objectMapper.writeValue(writer, event);

		JSONAssert.assertEquals("{id:\"1\", datacontenttype:\"application/json\"}", writer.toString(), false);
		JSONAssert.assertEquals("{data:{\"name\":\"Jacques\"}}", writer.toString(), false);
		
		objectMapper.readValue(writer.toString(), CloudEvent.class);
	}
	
	@Test
	void testSerDeserList() throws IOException, Exception {
		
		CloudEvent event = new CloudEventBuilder()
				.withAggregateId("1")
				.withAggregateType("test")
				.withTimestamp("2011-03-09T18:36:30+02:00")
				.withVersion(1)
				.withPayload(SimpleMessage.newBuilder().setName("Jacques").build())
				.build();
		
		StringWriter writer = new StringWriter();
		objectMapper.writeValue(writer, ImmutableList.of(event));
		
    	List<CloudEvent> cloudEvents = objectMapper.readValue(writer.toString(), new TypeReference<List<CloudEvent>>(){});
    	
    	assertEquals(1, cloudEvents.size());
    	assertEquals("test", cloudEvents.get(0).type());
	}
	
}
