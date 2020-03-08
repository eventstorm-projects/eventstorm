package eu.eventstorm.eventstore.ex;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.json.DeserializerException;
import eu.eventstorm.eventstore.util.ParserConsumer;

@SuppressWarnings("serial")
public final class UserCreatedEventPayloadStdDeserializer extends StdDeserializer<UserCreatedEventPayload> {

    private static final ImmutableMap<String, ParserConsumer<UserCreatedEventPayloadBuilder>> FIELDS;
    static {
        FIELDS = ImmutableMap.<String, ParserConsumer<UserCreatedEventPayloadBuilder>>builder()
                .put("name", (parser, builder) -> builder.name(parser.nextTextValue()))
                .put("email", (parser, builder) -> builder.email(parser.nextTextValue()))
                .put("age", (parser, builder) -> builder.age(parser.nextIntValue(0)))
                .build();
    }

    UserCreatedEventPayloadStdDeserializer() {
        super(UserCreatedEventPayload.class);
    }

    @Override
    public UserCreatedEventPayload deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (JsonToken.START_OBJECT != p.currentToken()) {
            throw new DeserializerException(DeserializerException.Type.PARSE_ERROR, ImmutableMap.of("expected",JsonToken.START_OBJECT,"current", p.currentToken()));
        }
        UserCreatedEventPayloadBuilder builder = new UserCreatedEventPayloadBuilder();
        p.nextToken();
        while (p.currentToken() != JsonToken.END_OBJECT) {
            if (JsonToken.FIELD_NAME != p.currentToken()) {
                throw new DeserializerException(DeserializerException.Type.PARSE_ERROR, ImmutableMap.of("expected",JsonToken.FIELD_NAME,"current", p.currentToken()));
            }
            ParserConsumer<UserCreatedEventPayloadBuilder> consumer = FIELDS.get(p.currentName());
            if (consumer == null) {
                throw new DeserializerException(DeserializerException.Type.FIELD_NOT_FOUND, ImmutableMap.of("field",p.currentName(),"command", "CreateMissionObjectCodeCommand"));
            }
            consumer.accept(p, builder);
            p.nextToken();
        }
        return builder.build();
     }
}