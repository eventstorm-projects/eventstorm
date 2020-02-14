package eu.eventstorm.core.id;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.id.SequenceGenerator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class SqlSequenceAggregateIdGenerator<T> implements AggregateIdGenerator {

    final SequenceGenerator<T> sequenceGenerator;
    
	public SqlSequenceAggregateIdGenerator(SequenceGenerator<T> sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    static final class Long extends SqlSequenceAggregateIdGenerator<java.lang.Long> {

        public Long(SequenceGenerator<java.lang.Long> sequenceGenerator) {
            super(sequenceGenerator);
        }
        
        @Override
        public AggregateId generate() {
            AggregateId aggregateId;
            try (Transaction tx = sequenceGenerator.getDatabase().transactionManager().newTransactionReadOnly()) {
                aggregateId = AggregateIds.from(sequenceGenerator.next());
                tx.rollback();
            }
            return aggregateId;
        }
	    
	}
    
    static final class Integer extends SqlSequenceAggregateIdGenerator<java.lang.Integer> {

        public Integer(SequenceGenerator<java.lang.Integer> sequenceGenerator) {
            super(sequenceGenerator);
        }
        
        @Override
        public AggregateId generate() {
            AggregateId aggregateId;
            try (Transaction tx = sequenceGenerator.getDatabase().transactionManager().newTransactionReadOnly()) {
                aggregateId = AggregateIds.from(sequenceGenerator.next());
                tx.rollback();
            }
            return aggregateId;
        }
        
    }
}
