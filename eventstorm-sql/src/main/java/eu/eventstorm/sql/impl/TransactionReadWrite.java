package eu.eventstorm.sql.impl;

import static eu.eventstorm.sql.impl.TransactionException.Type.CREATE_LOB;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.SqlQuery;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class TransactionReadWrite extends AbstractTransaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionReadWrite.class);

    private final Map<String, PreparedStatement> writes = new HashMap<>();

    TransactionReadWrite(TransactionManagerImpl transactionManager, Connection connection) {
        super(transactionManager, connection);
    }

    @Override
    public final boolean isReadOnly() {
        return false;
    }

    @Override
    protected final void doCommit() throws SQLException {
        getConnection().commit();
    }

    @Override
    public final TransactionQueryContext write(SqlQuery query) {
        return preparedStatement(query, this.writes, Statement.NO_GENERATED_KEYS);
    }
    
    @Override
   	public final TransactionQueryContext writeAutoIncrement(SqlQuery query) {
    	return preparedStatement(query, this.writes, Statement.RETURN_GENERATED_KEYS);
   	} 

	public final TransactionSupport innerTransaction(TransactionDefinition definition) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("innerTransaction : [{}]", definition);
        }
        return new TransactionNested(this, getTransactionManager());
    }
	
	@Override
	public Blob createBlob() {
		try {
			return getConnection().createBlob();
		} catch (SQLException cause) {
			throw new TransactionException(CREATE_LOB, this, null, cause);
		}
	}

	@Override
	public Clob createClob() {
		try {
			return getConnection().createClob();
		} catch (SQLException cause) {
			throw new TransactionException(CREATE_LOB, this, null, cause);
		}
	}

}
