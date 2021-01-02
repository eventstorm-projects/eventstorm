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

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.cloudevents.json.jackson.CloudEventDeserializerException.Type.PARSE_ERROR;
import static eu.eventstorm.cloudevents.json.jackson.CloudEventDeserializerException.Type.INVALID_FIELD_VALUE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CloudEventDeserializer extends StdDeserializer<CloudEvent> {

	public static final String FIELD = "field";

	private static final ImmutableMap<String, BiConsumer<JsonParser, CloudEventBuilder>> CONFIG;

	static {
		CONFIG = ImmutableMap.<String, BiConsumer<JsonParser, CloudEventBuilder>>builder()
				// @formatter:off
				.put("specversion", (parser, builder) -> builder.withSpecVersion(parseString(parser,"specversion")))
				.put("type", (parser, builder) ->  builder.withAggregateType(parseString(parser, "type")))
				.put("time", (parser, builder) -> builder.withTimestamp(parseString(parser, "time")))
				.put("id", (parser, builder) -> builder.withAggregateId(parseString(parser, "id")))
				.put("version", (parser, builder) -> {
					try {
						builder.withVersion(parser.nextIntValue(0));
					} catch (IOException cause) {
						throw new CloudEventDeserializerException(PARSE_ERROR, of(FIELD, "version"), cause);
					}
				})
				.put("subject", (parser, builder) -> builder.withSubject(parseString(parser, "subject")))
				.put("datacontenttype", (parser, builder) -> builder.withDataContentType(parseString(parser, "datacontenttype")))
				.put("data", (parser, builder) -> {
					try {
						// go to inside of data
						parser.nextToken();
						builder.withPayload(parser.readValueAs(Map.class));
					} catch (IOException cause) {
						throw new CloudEventDeserializerException(PARSE_ERROR, of(FIELD, "data"), cause);
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
			// exception.
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

	private static String parseString(JsonParser parser, String field) {
		try {
			if (parser.nextToken() == JsonToken.VALUE_STRING) {
				return parser.getText();
			} else if (parser.currentToken() == JsonToken.VALUE_NULL) {
				return null;
			}
			else {
				throw new CloudEventDeserializerException(INVALID_FIELD_VALUE, of(FIELD, field, "jsonToken", parser.currentToken()));
			}
		} catch (IOException cause) {
			throw new CloudEventDeserializerException(PARSE_ERROR, of(FIELD, field), cause);
		}
	}
}
