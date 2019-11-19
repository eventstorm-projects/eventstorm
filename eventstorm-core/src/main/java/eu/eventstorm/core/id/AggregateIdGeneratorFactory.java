package eu.eventstorm.core.id;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class AggregateIdGeneratorFactory {

	private AggregateIdGeneratorFactory() {
	}
	
	public static AggregateIdGenerator inMemoryInteger() {
		return new InMemoryIntegerAggregateIdGenerator(); 
	}
}
