package eu.eventstorm.core.id;

import eu.eventstorm.core.StreamId;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.id.SequenceGenerator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class SqlSequenceStreamIdGenerator<T> implements StreamIdGenerator {

    final SequenceGenerator<T> sequenceGenerator;
    
	public SqlSequenceStreamIdGenerator(SequenceGenerator<T> sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    static final class Long extends SqlSequenceStreamIdGenerator<java.lang.Long> {

        public Long(SequenceGenerator<java.lang.Long> sequenceGenerator) {
            super(sequenceGenerator);
        }
        
        @Override
        public StreamId generate() {
            StreamId aggregateId;
            try (Transaction tx = sequenceGenerator.getDatabase().transactionManager().newTransactionReadOnly()) {
                aggregateId = StreamIds.from(sequenceGenerator.next());
                tx.rollback();
            }
            return aggregateId;
        }
	    
	}
    
    static final class Integer extends SqlSequenceStreamIdGenerator<java.lang.Integer> {

        public Integer(SequenceGenerator<java.lang.Integer> sequenceGenerator) {
            super(sequenceGenerator);
        }
        
        @Override
        public StreamId generate() {
            StreamId aggregateId;
            try (Transaction tx = sequenceGenerator.getDatabase().transactionManager().newTransactionReadOnly()) {
                aggregateId = StreamIds.from(sequenceGenerator.next());
                tx.rollback();
            }
            return aggregateId;
        }
        
    }
}
