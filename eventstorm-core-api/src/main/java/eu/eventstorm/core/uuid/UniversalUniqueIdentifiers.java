package eu.eventstorm.core.uuid;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class UniversalUniqueIdentifiers {

	private static final AtomicInteger COUNTER = new AtomicInteger(0);
	
	private UniversalUniqueIdentifiers() {
	}
	
	public static UniversalUniqueIdentifierV6 generate(UniversalUniqueIdentifierDefinition definition) {
		return UniversalUniqueIdentifierV6.from(definition.getRegion(), definition.getNode(), COUNTER.incrementAndGet());
	}
	
}
