package eu.eventstorm.eventstore.memory;

import java.io.IOException;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import eu.eventstorm.eventstore.EventStoreException;
import org.springframework.core.io.buffer.DataBuffer;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.util.JsonFormat;

import eu.eventstorm.eventstore.StreamEventDefinition;

final class InMemoryStreamEventDefinition<T extends AbstractMessage> implements StreamEventDefinition {

	private final String stream;
	private final Class<T> eventClass;
	
	private final Parser<T> parser;
	private final Supplier<Message.Builder> supplier;
	
	private final JsonFormat.Parser jsonParser;
	
	InMemoryStreamEventDefinition(String stream, Class<T> eventClass, Descriptor descriptor, Parser<T> parser, Supplier<Message.Builder> supplier) {
		this.stream = stream;
		this.eventClass = eventClass;
		this.parser = parser;
		this.supplier = supplier;
		this.jsonParser = JsonFormat.parser().usingTypeRegistry(JsonFormat.TypeRegistry.newBuilder().add(descriptor).build());
	}


	@Override
	public String getStream() {
		return stream;
	}

	@Override
	public Message parse(DataBuffer buffer) {
		try {
			return  this.parser.parseFrom(buffer.asByteBuffer());
		} catch (IOException cause) {
			throw new EventStoreException(EventStoreException.Type.FAILED_TO_DESERIALIZE, ImmutableMap.of("stream", stream,
					"definition", this), cause);
		}
	}
	

	@Override
	public String getEventType() {
		return this.eventClass.getSimpleName();
	}


	@Override
	public Message jsonParse(String json) {
		Message.Builder builder = supplier.get();
		try {
			this.jsonParser.merge(json, builder);
		} catch (InvalidProtocolBufferException cause) {
			throw new EventStoreException(EventStoreException.Type.FAILED_TO_DESERIALIZE, ImmutableMap.of("stream", stream,
					"definition", this), cause);
		}
		return builder.build();
	}

}
