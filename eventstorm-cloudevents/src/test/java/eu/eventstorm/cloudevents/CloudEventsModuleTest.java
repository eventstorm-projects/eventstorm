package eu.eventstorm.cloudevents;

import java.io.IOException;
import java.io.StringWriter;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.cloudevents.json.jackson.CloudEventsModule;

class CloudEventsModuleTest {

	private ObjectMapper objectMapper;
	
	@BeforeEach
	void beforeEach() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new CloudEventsModule());
	}
	
	@Test
	void testSerDeser() throws IOException, Exception {
		
//		Temp temp = new Temp();
//		temp.setKey("key01");
//		temp.setValue("valuz01");
		
		CloudEvent event = new CloudEventBuilder()
				.withAggregateId("1")
				.withAggregateType("test")
				.withTimestamp("2011-03-09T18:36:30+02:00")
				.withVersion(1)
			//	.withPayload()
				.build();
		
//		StringWriter writer = new StringWriter();
//		objectMapper.writeValue(writer, event);
//
//		JSONAssert.assertEquals("{id:\"1\", datacontenttype:\"application/json\"}", writer.toString(), false);
//		JSONAssert.assertEquals("{data:{\"key\":\"key01\",\"value\":\"valuz01\"}}", writer.toString(), false);
//		
//		objectMapper.readValue(writer.toString(), CloudEvent.class);
	}
	
	
}
