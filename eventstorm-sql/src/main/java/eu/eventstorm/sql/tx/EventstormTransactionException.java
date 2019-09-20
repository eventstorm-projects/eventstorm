package eu.eventstorm.sql.tx;

import java.sql.SQLException;

import eu.eventstorm.sql.EventstormSqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventstormTransactionException extends EventstormSqlException {

    /**
	 *  Generated seruql UID.
	 */
	private static final long serialVersionUID = -7353728745195001854L;

	public enum Type {
		PREPARED_STATEMENT, CREATE, NO_CURRENT_TRANSACTION,  READ_ONLY, CONNECTION_ISOLATION, NOT_ACTIVE, COMMIT , ROLLBACK
    }

    private final Type type;

    EventstormTransactionException(Type type, AbstractTransaction transaction, TransactionSpan span) {
        super("");
        this.type = type;
    }

    EventstormTransactionException(Type type, AbstractTransaction transaction, TransactionSpan span, SQLException cause) {
        super(cause);
        this.type = type;
    }

    
    EventstormTransactionException(Type type) {
        super("");
        this.type = type;
    }

     EventstormTransactionException(Type type, SQLException cause) {
        super(cause);
        this.type = type;
    }

	public Type getType() {
        return type;
    }

}