package eu.eventstorm.core.id;

import java.util.concurrent.atomic.AtomicInteger;

import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryIntegerStreamIdGenerator implements StreamIdGenerator {

	private final AtomicInteger counter = new AtomicInteger();

	@Override
	public StreamId generate() {
		return new IntegerStreamId(counter.incrementAndGet());
	}

}
