package eu.eventstorm.core.json.jackson;

import java.io.IOException;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.cloudevent.CloudEvent;
import eu.eventstorm.core.cloudevent.CloudEventBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings({ "serial" })
final class CloudEventDeserializer extends StdDeserializer<CloudEvent> {

	private static final ImmutableMap<String, BiConsumer<JsonParser, CloudEventBuilder>> CONFIG;
	
	static {
		CONFIG = ImmutableMap.<String, BiConsumer<JsonParser, CloudEventBuilder>>builder()
			.put("specversion", (parser, builder) -> {
				try {
					builder.withSpecVersion(parser.getText());
				} catch (IOException cause) {
					throw new CloudEventDeserializerException(CloudEventDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("field","specversion"), cause);
				}
			})
			.build();
	}
	
	CloudEventDeserializer() {
		super(CloudEvent.class);
	}

	@Override
	public CloudEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		CloudEventBuilder builder = new CloudEventBuilder();
		 JsonToken t = p.getCurrentToken();
		 
		 if (t == JsonToken.START_OBJECT) {
			 p.nextToken();
		 } else {
			 // exeception.
		 }
		 
		 t = p.getCurrentToken();
		 if (t == JsonToken.FIELD_NAME) {
			 
			 String fieldName = p.getText();
			 
			 BiConsumer<JsonParser, CloudEventBuilder> consumer = CONFIG.get(fieldName);
			 
			 consumer.accept(p, builder);
			 
			 
			 p.nextToken();
		 }
		 
		return builder.build();
	}

}
