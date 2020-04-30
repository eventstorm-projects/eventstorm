package eu.eventstorm.eventstore;

import org.springframework.core.io.buffer.DataBuffer;

import com.google.protobuf.AbstractMessage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface StreamEventDefinition {
	
	String getStream();

	AbstractMessage parse(DataBuffer buffer);

	String getEventType();

}