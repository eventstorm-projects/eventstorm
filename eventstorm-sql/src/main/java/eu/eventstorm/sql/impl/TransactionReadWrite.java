package eu.eventstorm.sql.impl;

import java.sql.Connection;
import java.sql.Statement;

import eu.eventstorm.sql.TransactionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.SqlQuery;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class TransactionReadWrite extends AbstractTransaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionReadWrite.class);

    TransactionReadWrite(TransactionManagerImpl transactionManager, Connection connection, TransactionDefinition definition) {
        super(transactionManager, connection, definition);
    }

    @Override
    public final boolean isReadOnly() {
        return false;
    }

    @Override
    public final TransactionQueryContext write(SqlQuery query) {
        return preparedStatement(query, getStatements(), Statement.NO_GENERATED_KEYS);
    }
    
    @Override
   	public final TransactionQueryContext writeAutoIncrement(SqlQuery query) {
    	return preparedStatement(query, getStatements(), Statement.RETURN_GENERATED_KEYS);
   	} 

	public final TransactionSupport innerTransaction(TransactionDefinition definition) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("innerTransaction : [{}]", definition);
        }
        return new TransactionNested(this, getTransactionManager());
    }

}
