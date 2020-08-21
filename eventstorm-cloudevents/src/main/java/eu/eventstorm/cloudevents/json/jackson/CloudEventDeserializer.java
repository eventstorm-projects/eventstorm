package eu.eventstorm.cloudevents.json.jackson;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.cloudevents.CloudEventBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings({ "serial" })
final class CloudEventDeserializer extends StdDeserializer<CloudEvent> {

	private static final ImmutableMap<String, BiConsumer<JsonParser, CloudEventBuilder>> CONFIG;

	static {
		CONFIG = ImmutableMap.<String, BiConsumer<JsonParser, CloudEventBuilder>>builder()
				// @formatter:off
			.put("specversion", (parser, builder) -> {
				try {
					builder.withSpecVersion(parser.nextTextValue());
					
				} catch (IOException cause) {
					throw new CloudEventDeserializerException(CloudEventDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("field", "specversion"), cause);
				}
			})
			.put("type", (parser, builder) -> {
				try {
					builder.withAggregateType(parser.nextTextValue());
				} catch (IOException cause) {
					throw new CloudEventDeserializerException(CloudEventDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("field", "type"), cause);
				}
			})
			.put("id", (parser, builder) -> {
				try {
					builder.withAggregateId(parser.nextTextValue());
				} catch (IOException cause) {
					throw new CloudEventDeserializerException(CloudEventDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("field", "id"), cause);
				}
			})
			.put("time", (parser, builder) -> {
				try {
					builder.withTimestamp(parser.nextTextValue());
				} catch (IOException cause) {
					throw new CloudEventDeserializerException(CloudEventDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("field", "time"), cause);
				}
			})
			.put("version", (parser, builder) -> {
				try {
					builder.withVersion(parser.nextIntValue(0));
				} catch (IOException cause) {
					throw new CloudEventDeserializerException(CloudEventDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("field", "version"), cause);
				}
			})
			.put("subject", (parser, builder) -> {
				try {
					builder.withSubject(parser.nextTextValue());
				} catch (IOException cause) {
					throw new CloudEventDeserializerException(CloudEventDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("field", "subject"), cause);
				}
			})
			.put("datacontenttype", (parser, builder) -> {
				try {
					builder.withDataContentType(parser.nextTextValue());
				} catch (IOException cause) {
					throw new CloudEventDeserializerException(CloudEventDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("field", "datacontenttype"), cause);
				}
			})
			.put("data", (parser, builder) -> {
				try {
					// go to inside of data
					parser.nextToken();
					builder.withPayload(parser.readValueAs(Map.class));
				} catch (IOException cause) {
					throw new CloudEventDeserializerException(CloudEventDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("field", "data"), cause);
				}
			})
			// @formatter:on
			.build();
	}

	CloudEventDeserializer() {
		super(CloudEvent.class);
	}

	@Override
	public CloudEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

		CloudEventBuilder builder = new CloudEventBuilder();
		JsonToken t = p.getCurrentToken();

		if (t == JsonToken.START_OBJECT) {
			p.nextToken();
		} else {
			// exeception.
		}

		while (t != JsonToken.END_OBJECT) {
			t = p.getCurrentToken();
			if (t == JsonToken.FIELD_NAME) {
				String fieldName = p.getText();
				BiConsumer<JsonParser, CloudEventBuilder> consumer = CONFIG.get(fieldName);
				if (consumer == null) {
					throw new UnsupportedOperationException("field [" + fieldName + "] not supported for cloudEvent");
				}
				consumer.accept(p, builder);
				t = p.nextToken();
			}	
		}
		return builder.build();
	}

}
