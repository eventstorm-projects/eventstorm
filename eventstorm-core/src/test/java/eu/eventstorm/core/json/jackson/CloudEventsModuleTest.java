package eu.eventstorm.core.json.jackson;

import java.io.IOException;
import java.io.StringWriter;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.id.AggregateIds;
import eu.eventstorm.core.impl.Events;

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
		Event event = Events.newEvent(AggregateIds.from(1), "test", OffsetDateTime.now(), 1, temp);
		
		StringWriter writer = new StringWriter();
		objectMapper.writeValue(writer, event);
		
		objectMapper.readValue(writer.toString(), Event.class);
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
