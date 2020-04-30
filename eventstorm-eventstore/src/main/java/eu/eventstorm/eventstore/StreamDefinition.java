package eu.eventstorm.eventstore;

import com.google.protobuf.AbstractMessage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface StreamDefinition {
	
	String getName();

	<T extends AbstractMessage> StreamEventDefinition getStreamEventDefinition(String payloadType);
	
}