package eu.eventstorm.core.id;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class StreamIdGeneratorManager {

	private ImmutableMap<String, StreamIdGenerator> generators;

	public StreamIdGeneratorManager(ImmutableMap<String, StreamIdGenerator> generators) {
		this.generators = generators;
	}

	public StreamIdGenerator getAggregateIdGenerator(String domain) {
		return generators.get(domain);
	}

}
