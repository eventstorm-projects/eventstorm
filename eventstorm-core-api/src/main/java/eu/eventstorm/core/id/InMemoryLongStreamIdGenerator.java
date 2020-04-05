package eu.eventstorm.core.id;

import java.util.concurrent.atomic.AtomicLong;

import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryLongStreamIdGenerator implements StreamIdGenerator {

	private final AtomicLong counter = new AtomicLong();

	@Override
	public StreamId generate() {
		return new LongStreamId(counter.incrementAndGet());
	}

}
