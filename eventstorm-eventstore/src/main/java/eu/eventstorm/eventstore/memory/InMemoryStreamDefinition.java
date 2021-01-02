package eu.eventstorm.eventstore.memory;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamDefinitionException;
import eu.eventstorm.eventstore.StreamEventDefinition;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryStreamDefinition implements StreamDefinition {

	private final String name;
	
	private final ImmutableMap<String, InMemoryStreamEventDefinition<?>> mapByEventPayloadType;

	public InMemoryStreamDefinition(String name, List<InMemoryStreamEventDefinition<?>> defs) {
		this.name = name;
		this.mapByEventPayloadType = defs.stream().collect(toImmutableMap(InMemoryStreamEventDefinition::getEventType, identity()));
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public StreamEventDefinition getStreamEventDefinition(String event) {
		StreamEventDefinition def = mapByEventPayloadType.get(event);

		if (def == null) {
			throw StreamDefinitionException.newUnknownStreamType(name, event);
		}

		return def;
	}

}
