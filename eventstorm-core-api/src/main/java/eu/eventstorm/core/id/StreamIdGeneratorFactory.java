package eu.eventstorm.core.id;

import eu.eventstorm.sql.id.SequenceGenerator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class StreamIdGeneratorFactory {

    private StreamIdGeneratorFactory() {
    }

    public static StreamIdGenerator inMemoryInteger() {
        return new InMemoryIntegerStreamIdGenerator();
    }

    public static StreamIdGenerator inMemoryLong() {
        return new InMemoryLongStreamIdGenerator();
    }

    public static StreamIdGenerator sequenceInteger(SequenceGenerator<Integer> sequenceGenerator) {
        return new SqlSequenceStreamIdGenerator.Integer(sequenceGenerator);
    }

    public static StreamIdGenerator sequenceLong(SequenceGenerator<Long> sequenceGenerator) {
        return new SqlSequenceStreamIdGenerator.Long(sequenceGenerator);
    }
    
    public static StreamIdGenerator uuid(UniversalUniqueIdentifierDefinition definition) {
        return new UniversalUniqueIdentifierStreamIdGenerator(definition);
    }
}
