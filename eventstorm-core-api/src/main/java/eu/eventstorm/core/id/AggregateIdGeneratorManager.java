package eu.eventstorm.core.id;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class AggregateIdGeneratorManager {

	private ImmutableMap<String, AggregateIdGenerator> generators;

	public AggregateIdGeneratorManager(ImmutableMap<String, AggregateIdGenerator> generators) {
		this.generators = generators;
	}

	public AggregateIdGenerator getAggregateIdGenerator(String domain) {
		return generators.get(domain);
	}

}
