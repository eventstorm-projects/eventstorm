package eu.eventstorm.eventstore;

public interface StreamManager {
	
	StreamDefinition getDefinition(String stream);

}
