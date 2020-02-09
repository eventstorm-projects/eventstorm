package eu.eventstorm.sql.impl;

import java.sql.SQLException;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.EventstormSqlExceptionType;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.tracer.TransactionSpan;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionException extends EventstormSqlException {

    /**
	 *  Generated seruql UID.
	 */
	private static final long serialVersionUID = -7353728745195001854L;

	public enum Type implements EventstormSqlExceptionType {
		PREPARED_STATEMENT, CREATE, NO_CURRENT_TRANSACTION,  READ_ONLY, CONNECTION_ISOLATION, NOT_ACTIVE, COMMIT , ROLLBACK
    }


    public TransactionException(Type type, Transaction transaction) {
        super(type, null);
    }

    TransactionException(Type type, Transaction transaction, TransactionSpan span, SQLException cause) {
        super(type, null, cause);
    }

    
    TransactionException(Type type) {
        super(type, null);
    }

     TransactionException(Type type, SQLException cause) {
        super(type, null, cause);
    }

}