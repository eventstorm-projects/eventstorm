package eu.eventstorm.eventstore;

import org.springframework.core.io.buffer.DataBuffer;

import com.google.protobuf.Message;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface StreamEventDefinition {

	String getStream();

	Message parse(DataBuffer buffer);

	Message jsonParse(String json);

	String getEventType();

}