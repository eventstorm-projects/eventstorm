package eu.eventstorm.core.id;

import java.util.concurrent.atomic.AtomicInteger;

import eu.eventstorm.core.AggregateId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryIntegerAggregateIdGenerator implements AggregateIdGenerator {

	private final AtomicInteger counter = new AtomicInteger();

	@Override
	public AggregateId generate() {
		return new IntegerAggregateId(counter.incrementAndGet());
	}

}
