package eu.eventstorm.eventstore.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryStreamManagerBuilder {

	private final List<InMemoryStreamDefinitionBuilder> builders = new ArrayList<>();

	public InMemoryStreamDefinitionBuilder withDefinition(String stream) {
		InMemoryStreamDefinitionBuilder builder = new InMemoryStreamDefinitionBuilder(this, stream);
		builders.add(builder);
		return builder;
	}

	public InMemoryStreamManager build() {
		try (Stream<InMemoryStreamDefinitionBuilder> stream = builders.stream()) {
			return new InMemoryStreamManager(stream.collect(ImmutableMap.toImmutableMap(InMemoryStreamDefinitionBuilder::getStream, InMemoryStreamDefinitionBuilder::build)));	
		}
	}

}