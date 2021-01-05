package eu.eventstorm.sql.impl;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionDefinitions {

    public static final TransactionDefinition READ_ONLY = new TransactionDefinition() {
        @Override
        public boolean isReadOnly() {
            return true;
        }
    };

    public static final TransactionDefinition READ_WRITE = new TransactionDefinition() {
    };

    public static final TransactionDefinition ISOLATED_READ_WRITE = new TransactionDefinition() {
    };

}
