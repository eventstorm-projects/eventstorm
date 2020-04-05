package eu.eventstorm.eventstore.memory;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryStreamManager implements StreamManager {

	private final ImmutableMap<String, InMemoryStreamDefinition> map;

	public InMemoryStreamManager(ImmutableMap<String, InMemoryStreamDefinition> map) {
		this.map = map;
	}

	@Override
	public StreamDefinition getDefinition(String stream) {
		return map.get(stream);
	}

}