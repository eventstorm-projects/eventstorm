package eu.eventstorm.eventstore.ex;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.json.Serializer;
import eu.eventstorm.core.json.SerializerException;

public final class UserCreatedEventPayloadSerializer implements Serializer<UserCreatedEventPayload> {

    private final ObjectMapper objectMapper;

    public UserCreatedEventPayloadSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(UserCreatedEventPayload payload) {
        try {
            return objectMapper.writeValueAsBytes(payload);
        } catch (IOException cause) {
            throw new SerializerException(SerializerException.Type.WRITE_ERROR, ImmutableMap.of("payload", payload));
        }
     }
}