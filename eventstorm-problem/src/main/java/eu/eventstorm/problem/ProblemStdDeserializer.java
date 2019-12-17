package eu.eventstorm.problem;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings("serial")
final class ProblemStdDeserializer extends StdDeserializer<Problem> {

	public static final ProblemStdDeserializer INSTANCE = new ProblemStdDeserializer();

	private static final Map<String, BiConsumer<ProblemBuilder, String>> READER = ImmutableMap.<String, BiConsumer<ProblemBuilder, String>>builder()
			.put("type", (builder, value) -> builder.withType(URI.create(value)))
			.put("instance", (builder, value) -> builder.withInstance(URI.create(value)))
			.put("title", (builder, value) -> builder.withTitle(value))
			.put("detail", (builder, value) -> builder.withDetail(value))
			.put("trace-id", (builder, value) -> builder.withTraceId(value))
			.put("status", (builder, value) -> builder.withStatus(Integer.valueOf(value)))
			.put("timestamp", (builder, value) -> builder.withTimestamp(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value, OffsetDateTime::from)))
			.build();
	
	private ProblemStdDeserializer() {
		super(Problem.class);
	}

	@Override
	public Problem deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		ProblemBuilder builder = Problem.builder();
		parser.nextToken();
		while (!JsonToken.END_OBJECT.equals(parser.currentToken())) {
			String field = parser.getCurrentName();
			BiConsumer<ProblemBuilder, String> consumer = getConsumer(field);
			parser.nextToken();
			consumer.accept(builder, parser.getText());
			parser.nextToken();
		}
		return builder.build();
	}
	
	
	private BiConsumer<ProblemBuilder, String> getConsumer(String field) {
		BiConsumer<ProblemBuilder, String> consumer = READER.get(field);
		if (consumer != null) {
			return consumer;
		}
		return (builder,value) -> builder.with(field, value);
	}
	

}
