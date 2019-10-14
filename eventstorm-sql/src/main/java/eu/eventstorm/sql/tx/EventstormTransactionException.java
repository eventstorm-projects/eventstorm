package eu.eventstorm.sql.tx;

import java.sql.SQLException;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.EventstormSqlExceptionType;
import eu.eventstorm.sql.tx.tracer.TransactionSpan;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventstormTransactionException extends EventstormSqlException {

    /**
	 *  Generated seruql UID.
	 */
	private static final long serialVersionUID = -7353728745195001854L;

	public enum Type implements EventstormSqlExceptionType {
		PREPARED_STATEMENT, CREATE, NO_CURRENT_TRANSACTION,  READ_ONLY, CONNECTION_ISOLATION, NOT_ACTIVE, COMMIT , ROLLBACK
    }


    EventstormTransactionException(Type type, AbstractTransaction transaction, TransactionSpan span) {
        super(type, null);
    }

    EventstormTransactionException(Type type, AbstractTransaction transaction, TransactionSpan span, SQLException cause) {
        super(type, null, cause);
    }

    
    EventstormTransactionException(Type type) {
        super(type, null);
    }

     EventstormTransactionException(Type type, SQLException cause) {
        super(type, null, cause);
    }

}