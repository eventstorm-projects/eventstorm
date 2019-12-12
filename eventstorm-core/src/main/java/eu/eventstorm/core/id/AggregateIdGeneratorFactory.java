package eu.eventstorm.core.id;

import eu.eventstorm.sql.id.SequenceGenerator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class AggregateIdGeneratorFactory {

    private AggregateIdGeneratorFactory() {
    }

    public static AggregateIdGenerator inMemoryInteger() {
        return new InMemoryIntegerAggregateIdGenerator();
    }

    public static AggregateIdGenerator inMemoryLong() {
        return new InMemoryLongAggregateIdGenerator();
    }

    public static AggregateIdGenerator sequenceInteger(SequenceGenerator<Integer> sequenceGenerator) {
        return new SqlSequenceAggregateIdGenerator.Integer(sequenceGenerator);
    }

    public static AggregateIdGenerator sequenceLong(SequenceGenerator<Long> sequenceGenerator) {
        return new SqlSequenceAggregateIdGenerator.Long(sequenceGenerator);
    }
}
