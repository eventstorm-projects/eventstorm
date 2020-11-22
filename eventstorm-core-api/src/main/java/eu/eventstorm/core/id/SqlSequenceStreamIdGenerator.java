package eu.eventstorm.core.id;

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
        public String generate() {
            String id;
            try (Transaction tx = sequenceGenerator.getDatabase().transactionManager().newTransactionReadOnly()) {
            	id = String.valueOf(sequenceGenerator.next());
                tx.rollback();
            }
            return id;
        }
	    
	}
    
    static final class Integer extends SqlSequenceStreamIdGenerator<java.lang.Integer> {

        public Integer(SequenceGenerator<java.lang.Integer> sequenceGenerator) {
            super(sequenceGenerator);
        }
        
        @Override
        public String generate() {
            String id;
            try (Transaction tx = sequenceGenerator.getDatabase().transactionManager().newTransactionReadOnly()) {
            	id = String.valueOf(sequenceGenerator.next());
                tx.rollback();
            }
            return id;
        }
        
    }
}
