package eu.eventstorm.cloudevents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringWriter;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.InvalidProtocolBufferException;
import eu.eventstorm.cloudevents.json.jackson.CloudEventDeserializerException;
import eu.eventstorm.core.Event;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.TypeRegistry;

import eu.eventstorm.cloudevents.json.jackson.CloudEventsModule;

@ExtendWith(LoggerInstancePostProcessor.class)
class CloudEventsModuleTest {

	private ObjectMapper objectMapper;
	
	@BeforeEach
	void beforeEach() {

		System.out.println(SimpleMessage.getDescriptor().getFullName());
		System.out.println(SimpleMessage.getDescriptor().getName());

		TypeRegistry typeRegistry = TypeRegistry.newBuilder()
				.add(SimpleMessage.getDescriptor())
				.build();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new CloudEventsModule(typeRegistry));

		try {
			Object o = typeRegistry.getDescriptorForTypeUrl("https://www/test/SimpleMessage");
			System.out.println(typeRegistry);
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}

	}
	
	@Test
	void testSerDeser() throws Exception {
		
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
	void testSerDeserAny() throws Exception {
		
		CloudEvent event = new CloudEventBuilder()
				.withAggregateId("1")
				.withAggregateType("test")
				.withTimestamp("2011-03-09T18:36:30+02:00")
				.withVersion(1)
				.withSubject("type.googleapis.com/" + SimpleMessage.getDescriptor().getFullName())
				.withPayload(Any.pack(SimpleMessage.newBuilder().setName("Jacques").build()))
				.build();
		
		StringWriter writer = new StringWriter();
		objectMapper.writeValue(writer, event);

		JSONAssert.assertEquals("{id:\"1\", datacontenttype:\"application/json\"}", writer.toString(), false);
		JSONAssert.assertEquals("{data:{\"name\":\"Jacques\"}}", writer.toString(), false);
		
		CloudEvent cloudEvent = objectMapper.readValue(writer.toString(), CloudEvent.class);

		SimpleMessage simpleMessage = (SimpleMessage) cloudEvent.data();
		assertEquals("Jacques", simpleMessage.getName());

		cloudEvent = objectMapper.readValue(writer.toString(), CloudEvent.class);
		assertEquals("Jacques", simpleMessage.getName());
	}
	
	@Test
	void testSerDeserList() throws Exception {

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

	@Test
	void failedToDeserializedTest() {

		CloudEventDeserializerException ex = assertThrows(CloudEventDeserializerException.class, () -> objectMapper.readValue("{\"specversion\":{}}", CloudEvent.class));
		assertEquals(CloudEventDeserializerException.Type.INVALID_FIELD_VALUE, ex.getType());

	}

	@Test
	void testSerDeserDataMap() throws Exception {

		CloudEvent event = new CloudEventBuilder()
				.withAggregateId("1")
				.withAggregateType("test")
				.withTimestamp("2011-03-09T18:36:30+02:00")
				.withVersion(1)
				.withPayload(ImmutableMap.of("key1","value1", "key2","value2"))
				.build();

		StringWriter writer = new StringWriter();
		objectMapper.writeValue(writer, event);

		JSONAssert.assertEquals("{id:\"1\", datacontenttype:\"application/json\"}", writer.toString(), false);
		JSONAssert.assertEquals("{data:{\"key1\":\"value1\"}}", writer.toString(), false);

		CloudEvent ev = objectMapper.readValue(writer.toString(), CloudEvent.class);

		System.out.println(ev.data());
	}

	@Test
	void testSerDeserDataNull() throws Exception {

		CloudEvent event = new CloudEventBuilder()
				.withAggregateId("1")
				.withAggregateType("test")
				.withTimestamp("2011-03-09T18:36:30+02:00")
				.withVersion(1)
				.build();

		StringWriter writer = new StringWriter();
		objectMapper.writeValue(writer, event);

		JSONAssert.assertEquals("{id:\"1\", datacontenttype:\"application/json\"}", writer.toString(), false);
		JSONAssert.assertEquals("{data:{}}", writer.toString(), false);

		objectMapper.readValue(writer.toString(), CloudEvent.class);
	}
}
