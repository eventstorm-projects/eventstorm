package eu.eventstorm.eventstore.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

public final class InMemoryStreamDefinitionBuilder {

	private final String stream;
	private final InMemoryStreamManagerBuilder builder;
	
	private final List<InMemoryStreamEventDefinition<?>> lists = new ArrayList<>();
	
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

	public <T extends AbstractMessage> InMemoryStreamDefinitionBuilder withPayload(Class<T> eventPayloadClass, Descriptor descriptor, Parser<T> parser, Supplier<Message.Builder> builder) {
		this.lists.add(new InMemoryStreamEventDefinition<T>(stream, eventPayloadClass, descriptor, parser, builder));
		return this;
	}
	
//	public <T extends EventPayload> InMemoryStreamDefinitionBuilder withPayload( Class<T> eventPayloadClass, Serializer<T> serializer, Deserializer<T> deserializer) {
//		return withPayload(eventPayloadClass.getSimpleName(), eventPayloadClass, serializer, deserializer);
//	}
//
//	public <T extends EventPayload> InMemoryStreamDefinitionBuilder withPayload(String type, Class<T> eventPayloadClass, Serializer<T> serializer, Deserializer<T> deserializer) {
//		this.lists.add(new InMemoryStreamEvantPayloadDefinition<T>(stream, type, eventPayloadClass, serializer, deserializer));
//		return this;
//	}
	
}
