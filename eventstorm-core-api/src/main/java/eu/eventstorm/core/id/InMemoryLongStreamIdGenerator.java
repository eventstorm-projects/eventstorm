package eu.eventstorm.core.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryLongStreamIdGenerator implements StreamIdGenerator {

	private final AtomicLong counter = new AtomicLong();

	@Override
	public String generate() {
		return String.valueOf(counter.incrementAndGet());
	}

}
