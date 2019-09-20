package eu.eventstorm.sql.tx;

import java.sql.Connection;
import java.sql.PreparedStatement;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionReadOnly extends AbstractTransaction {

    TransactionReadOnly(TransactionManagerImpl transactionManager, Connection connection) {
        super(transactionManager, connection);
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    protected void doCommit() {
        throw new EventstormTransactionException(EventstormTransactionException.Type.READ_ONLY);
    }

    @Override
    public PreparedStatement write(String sql) {
        throw new EventstormTransactionException(EventstormTransactionException.Type.READ_ONLY);
    }

    public Transaction innerTransaction(TransactionDefinition definition) {
        if (!definition.isReadOnly()) {
            throw new EventstormTransactionException(EventstormTransactionException.Type.READ_ONLY);
        }
        return new TransactionNested(this);
    }

}
