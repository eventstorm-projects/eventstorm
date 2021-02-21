package eu.eventstorm.problem;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ProblemStdDeserializer extends StdDeserializer<Problem> {

	public static final ProblemStdDeserializer INSTANCE = new ProblemStdDeserializer();

	private static final Map<String, BiConsumer<ProblemBuilder, String>> READER = ImmutableMap.<String, BiConsumer<ProblemBuilder, String>>builder()
			.put("type", (builder, value) -> builder.withType(URI.create(value)))
			.put("instance", (builder, value) -> builder.withInstance(URI.create(value)))
			.put("title", ProblemBuilder::withTitle)
			.put("detail", ProblemBuilder::withDetail)
			.put("traceId", ProblemBuilder::withTraceId)
			.put("status", (builder, value) -> builder.withStatus(Integer.parseInt(value)))
			.put("timestamp", (builder, value) -> builder.withTimestamp(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value, OffsetDateTime::from)))
			.build();
	
	private ProblemStdDeserializer() {
		super(Problem.class);
	}

	@Override
	public Problem deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
		ProblemBuilder builder = Problem.builder();
		parser.nextToken();
		while (!JsonToken.END_OBJECT.equals(parser.currentToken())) {
			String field = parser.getCurrentName();
			BiConsumer<ProblemBuilder, String> consumer = READER.get(field);
			if (consumer != null) {
				parser.nextToken();
				consumer.accept(builder, parser.getText());
				parser.nextToken();
			} else {
				doDeserializeCustomField(parser, field, builder);
			}
		}
		return builder.build();
	}

	private void doDeserializeCustomField(JsonParser parser, String field, ProblemBuilder builder) throws IOException {
		JsonToken token = parser.nextToken();

		if (token == JsonToken.VALUE_STRING) {
			builder.with(field, parser.getText());
			parser.nextToken();
			return;
		}

		if (token == JsonToken.VALUE_NULL) {
			builder.with(field, null);
			parser.nextToken();
			return;
		}
		if (token == JsonToken.VALUE_NUMBER_INT) {
			builder.with(field, parser.getValueAsInt());
			parser.nextToken();
			return;
		}
		if (token == JsonToken.START_ARRAY) {
			builder.with(field, parser.readValueAs(List.class));
			parser.nextToken();
			return;
		}
		if (token == JsonToken.START_OBJECT) {
			builder.with(field, parser.readValueAs(Map.class));
			parser.nextToken();
			return;
		}

		throw new IllegalStateException();
	}

}
