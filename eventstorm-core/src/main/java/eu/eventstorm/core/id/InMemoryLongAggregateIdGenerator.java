package eu.eventstorm.core.id;

import java.util.concurrent.atomic.AtomicLong;

import eu.eventstorm.core.AggregateId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryLongAggregateIdGenerator implements AggregateIdGenerator {

	private final AtomicLong counter = new AtomicLong();

	@Override
	public AggregateId generate() {
		return new LongAggreateId(counter.incrementAndGet());
	}

}
