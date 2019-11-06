package eu.eventstorm.sql.spring;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import eu.eventstorm.sql.tx.Transaction;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventstormTransactionStatus implements TransactionStatus {

	/** The new transction. */
	private boolean newTransaction;
	
	private final Transaction tx;
	
	private final TransactionDefinition definition;
	
	private final EventstormTransactionStatus previous;
	
	public EventstormTransactionStatus(Transaction tx, TransactionDefinition definition, boolean newTransaction, EventstormTransactionStatus previous) {
		this.newTransaction = newTransaction;
		this.tx = tx;
		this.definition = definition;
		this.previous = previous;
	}

	@Override
	public Object createSavepoint() throws TransactionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void releaseSavepoint(Object savepoint) throws TransactionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNewTransaction() {
		return newTransaction;
	}

	@Override
	public boolean hasSavepoint() {
		return false;
	}

	@Override
	public void setRollbackOnly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRollbackOnly() {
		return false;
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

	/**
	 * Checks if is read only.
	 *
	 * @return true, if is read only
	 */
	public boolean isReadOnly() {
		return this.definition.isReadOnly();
	}
	
	public TransactionDefinition getDefinition() {
		return definition;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(true);
		builder.append("newTransaction", newTransaction);
		builder.append("definition", definition);
		builder.append("transaction", tx);
		builder.append("previous", previous);
		return builder.toString();
	}
	
	public Transaction getTransaction() {
		return this.tx;
	}

	public EventstormTransactionStatus getPreviousTransaction() {
		return previous;
	}

}
