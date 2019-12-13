package eu.eventstorm.core.json.jackson;

import java.io.IOException;
import java.io.StringWriter;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.cloudevent.CloudEvent;
import eu.eventstorm.core.cloudevent.CloudEventBuilder;
import eu.eventstorm.core.id.AggregateIds;

class CloudEventsModuleTest {

	private ObjectMapper objectMapper;
	
	@BeforeEach
	void beforeEach() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new CloudEventsModule());
	}
	
	@Test
	void testSerDeser() throws IOException {
		
		Temp temp = new Temp();
		temp.setKey("key01");
		temp.setValue("valuz01");
		
		
		CloudEvent event = new CloudEventBuilder()
				.aggregateId(AggregateIds.from(1))
				.aggreateType("test")
				.timestamp(OffsetDateTime.now())
				.version(1)
				.payload(temp)
				.build();
		
		StringWriter writer = new StringWriter();
		objectMapper.writeValue(writer, event);
		
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
