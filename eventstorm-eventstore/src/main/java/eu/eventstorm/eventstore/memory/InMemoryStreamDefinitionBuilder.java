package eu.eventstorm.eventstore.memory;

import java.util.ArrayList;
import java.util.List;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.Serializer;

public final class InMemoryStreamDefinitionBuilder {

	private final String stream;
	private final InMemoryStreamManagerBuilder builder;
	
	private final List<InMemoryStreamEvantPayloadDefinition<?>> lists = new ArrayList<>();
	
	InMemoryStreamDefinitionBuilder(InMemoryStreamManagerBuilder builder, String stream) {
		this.stream = stream;
		this.builder = builder;
	}

	public InMemoryStreamManagerBuilder and() {
		return builder;
	}
	
	String getStream() {
		return stream;
	}
	
	InMemoryStreamDefinition build() {
		return new InMemoryStreamDefinition(this.stream, this.lists);
	}
	
	public <T extends EventPayload> InMemoryStreamDefinitionBuilder withPayload( Class<T> eventPayloadClass, Serializer<T> serializer, Deserializer<T> deserializer) {
		return withPayload(eventPayloadClass.getSimpleName(), eventPayloadClass, serializer, deserializer);
	}

	public <T extends EventPayload> InMemoryStreamDefinitionBuilder withPayload(String type, Class<T> eventPayloadClass, Serializer<T> serializer, Deserializer<T> deserializer) {
		this.lists.add(new InMemoryStreamEvantPayloadDefinition<T>(stream, type, eventPayloadClass, serializer, deserializer));
		return this;
	}
	
}
