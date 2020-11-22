package eu.eventstorm.core.id;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryIntegerStreamIdGenerator implements StreamIdGenerator {

	private final AtomicInteger counter = new AtomicInteger();

	@Override
	public String generate() {
		return String.valueOf(counter.incrementAndGet());
	}

}
