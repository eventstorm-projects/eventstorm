package eu.eventstorm.eventstore.memory;

import java.io.IOException;

import org.springframework.core.io.buffer.DataBuffer;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Parser;

import eu.eventstorm.eventstore.StreamEventDefinition;

final class InMemoryStreamEventDefinition<T extends AbstractMessage> implements StreamEventDefinition {

	private final String stream;
	private final Class<T> eventClass;
	private final Parser<T> parser;
//	
	InMemoryStreamEventDefinition(String stream, 
			Class<T> eventClass, Parser<T> parser
			) {
		this.stream = stream;
		this.eventClass = eventClass;
		this.parser = parser;
	}


	@Override
	public String getStream() {
		return stream;
	}

	@Override
	public AbstractMessage parse(DataBuffer buffer) {
		try {
			return  this.parser.parseFrom(buffer.asByteBuffer());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

//	@Override
//	public Class<T> getEventPayloadClass() {
//		return eventPayloadClass;
//	}
//
//	@Override
//	public Deserializer<T> getPayloadDeserializer() {
//		return deserializer;
//	}
//
//	@Override
//	public Serializer<T> getPayloadSerializer() {
//		return serializer;
//	}
//
	@Override
	public String getEventType() {
		return this.eventClass.getSimpleName();
	}

}
