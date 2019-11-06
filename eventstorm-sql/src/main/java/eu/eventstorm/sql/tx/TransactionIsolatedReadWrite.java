package eu.eventstorm.sql.tx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionIsolatedReadWrite extends AbstractTransaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIsolatedReadWrite.class);

    private final Map<String, PreparedStatement> writes = new HashMap<>();
    
    private final AbstractTransaction parent;

    TransactionIsolatedReadWrite(TransactionManagerImpl transactionManager, Connection connection, AbstractTransaction parent) {
        super(transactionManager, connection);
        this.parent = parent;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    protected void doCommit() throws SQLException {
        getConnection().commit();
    }

    @Override
    public TransactionQueryContext write(String sql) {
        return preparedStatement(sql, this.writes, Statement.NO_GENERATED_KEYS);
    }
    
    @Override
   	public TransactionQueryContext writeAutoIncrement(String sql) {
    	return preparedStatement(sql, this.writes, Statement.RETURN_GENERATED_KEYS);
   	} 

	public Transaction innerTransaction(TransactionDefinition definition) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("innerTransaction : [{}]", definition);
        }
        return new TransactionNested(this);
    }

}
