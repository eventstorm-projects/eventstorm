package eu.eventstorm.eventstore.memory;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.Serializer;
import eu.eventstorm.eventstore.StreamEvantPayloadDefinition;

final class InMemoryStreamEvantPayloadDefinition<T extends EventPayload> implements StreamEvantPayloadDefinition<T> {

	private final String stream;
	private final String type;
	private final Class<T> eventPayloadClass;
	private final Serializer<T> serializer;
	private final Deserializer<T> deserializer;
	
	InMemoryStreamEvantPayloadDefinition(String stream, String type, Class<T> eventPayloadClass, Serializer<T> serializer, Deserializer<T> deserializer) {
		this.stream = stream;
		this.type = type;
		this.eventPayloadClass = eventPayloadClass;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public String getStream() {
		return stream;
	}

	@Override
	public Class<T> getEventPayloadClass() {
		return eventPayloadClass;
	}

	@Override
	public Deserializer<T> getPayloadDeserializer() {
		return deserializer;
	}

	@Override
	public Serializer<T> getPayloadSerializer() {
		return serializer;
	}

	@Override
	public String getPayloadType() {
		return type;
	}

}
