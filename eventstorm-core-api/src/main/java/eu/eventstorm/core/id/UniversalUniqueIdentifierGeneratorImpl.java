package eu.eventstorm.core.id;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class UniversalUniqueIdentifierGeneratorImpl implements UniversalUniqueIdentifierGenerator {

	private final UniversalUniqueIdentifierDefinition definition;
	
	private final AtomicInteger counter = new AtomicInteger(0);
	
	UniversalUniqueIdentifierGeneratorImpl(UniversalUniqueIdentifierDefinition definition) {
		this.definition = definition;
	}

	@Override
	public UniversalUniqueIdentifier generate() {
		return UniversalUniqueIdentifierV6.from(definition.getRegion(), definition.getNode(), counter.incrementAndGet());
	}

}