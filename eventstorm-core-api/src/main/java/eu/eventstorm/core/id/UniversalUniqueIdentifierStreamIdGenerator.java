package eu.eventstorm.core.id;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class UniversalUniqueIdentifierStreamIdGenerator implements StreamIdGenerator {

	private final UniversalUniqueIdentifierGenerator generator;
	
	UniversalUniqueIdentifierStreamIdGenerator(UniversalUniqueIdentifierDefinition definition) {
		this.generator = new UniversalUniqueIdentifierGeneratorImpl(definition);
	}

	@Override
	public String generate() {
		return generator.generate().toString();
	}

}
