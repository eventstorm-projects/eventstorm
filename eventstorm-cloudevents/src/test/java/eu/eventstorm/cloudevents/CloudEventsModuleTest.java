package eu.eventstorm.cloudevents;

import java.io.IOException;
import java.io.StringWriter;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.cloudevents.json.jackson.CloudEventsModule;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.id.AggregateIds;

class CloudEventsModuleTest {

	private ObjectMapper objectMapper;
	
	@BeforeEach
	void beforeEach() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new CloudEventsModule());
	}
	
	@Test
	void testSerDeser() throws IOException, Exception {
		
		Temp temp = new Temp();
		temp.setKey("key01");
		temp.setValue("valuz01");
		
		CloudEvent event = new CloudEventBuilder()
				.withAggregateId(AggregateIds.from(1))
				.withAggregateType("test")
				.withTimestamp(OffsetDateTime.now())
				.withVersion(1)
				.withPayload(temp)
				.build();
		
		StringWriter writer = new StringWriter();
		objectMapper.writeValue(writer, event);

		JSONAssert.assertEquals("{id:\"1\", datacontenttype:\"application/json\"}", writer.toString(), false);
		JSONAssert.assertEquals("{data:{\"key\":\"key01\",\"value\":\"valuz01\"}}", writer.toString(), false);
		
		objectMapper.readValue(writer.toString(), CloudEvent.class);
	}
	
	
	private static class Temp implements EventPayload {
		private String key;
		private String value;
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
}
