package eu.eventstorm.core.json.jackson;

import java.io.IOException;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.impl.EventBuilder;

@SuppressWarnings({ "serial" })
public final class EventDeserializer extends StdDeserializer<Event> {

	private static final ImmutableMap<String, BiConsumer<JsonParser, EventBuilder>> CONFIG;
	
	static {
		CONFIG = ImmutableMap.<String, BiConsumer<JsonParser, EventBuilder>>builder()
			.put("specversion", (parser, builder) -> {
				try {
					builder.setSpecVersion(parser.getText());
				} catch (IOException e) {
					e.printStackTrace();
				}
			})
			.build();
	}
	
	EventDeserializer() {
		super(Event.class);
	}

	@Override
	public Event deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		EventBuilder builder = new EventBuilder();
		 JsonToken t = p.getCurrentToken();
		 
		 if (t == JsonToken.START_OBJECT) {
			 p.nextToken();
		 } else {
			 // exeception.
		 }
		 
		 t = p.getCurrentToken();
		 if (t == JsonToken.FIELD_NAME) {
			 
			 String fieldName = p.getText();
			 
			 BiConsumer<JsonParser, EventBuilder> consumer = CONFIG.get(fieldName);
			 
			 consumer.accept(p, builder);
			 
			 
			 p.nextToken();
		 }
		 
		return builder.build();
	}

}
