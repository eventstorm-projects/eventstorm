package eu.eventstorm.eventstore;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface StreamDefinition {
	
	String getName();

	StreamEventDefinition getStreamEventDefinition(String payloadType);
	
}