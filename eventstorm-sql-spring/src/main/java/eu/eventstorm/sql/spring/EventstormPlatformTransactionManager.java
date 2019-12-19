package eu.eventstorm.sql.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventstormPlatformTransactionManager implements PlatformTransactionManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventstormPlatformTransactionManager.class);

	private final TransactionManager transactionManager;

	public EventstormPlatformTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public TransactionStatus getTransaction(TransactionDefinition definition) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getTransaction({})", definition);
		}

        if (transactionManager.hasCurrent()) {
        	
            // get TX inside another TX
			if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
				// create a requires_new inside a existing one.
				return doBegin(definition, this.transactionManager.current());
			}
			
			EventstormTransactionStatus currentStatus = (EventstormTransactionStatus) TransactionSynchronizationManager.getResource(EventstormTransactionStatus.class);
			
			if (definition.getPropagationBehavior() < currentStatus.getDefinition().getPropagationBehavior()) {
				// -> create new TX
				if (LOGGER.isDebugEnabled()) {
					LOGGER.trace("create new TX inside another one due to propagation : current [{}], new [{}]", currentStatus.getDefinition().getPropagationBehavior(),
					        definition.getPropagationBehavior());
				}

				return doBegin(definition, transactionManager.current());

			} else {
				return new EventstormTransactionStatus(transactionManager.current(), definition, false, null);
			}


        } else {
            return doBegin(definition, null);
        }

	}

	@Override
	public void commit(TransactionStatus transactionStatus) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("commit({})", transactionStatus);
		}

		if (!transactionStatus.isNewTransaction()) {
			// It's a txStatus inside another txStatus
			return;
		}

		EventstormTransactionStatus status = (EventstormTransactionStatus) transactionStatus;

		if (status.isReadOnly()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Commit a read-only -> rollback");				
            }
            rollback(transactionStatus);
			return;
		}

		try {
			try {
				status.getTransaction().commit();
			} finally {
				status.getTransaction().close();
			}
		} finally {
			TransactionSynchronizationManager.unbindResource(EventstormTransactionStatus.class);
		}

		if (status.getPreviousTransaction() != null) {
			TransactionSynchronizationManager.bindResource(EventstormTransactionStatus.class, status);
		}

	}

	@Override
	public void rollback(TransactionStatus transactionStatus) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("rollback({})", transactionStatus);
		}
		if (!transactionStatus.isNewTransaction()) {
			// a transaction inside another one.
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("rollback({}) -> skip", transactionStatus);
			}
			return;
		}

		EventstormTransactionStatus status = (EventstormTransactionStatus) transactionStatus;

		try {
			try {
				status.getTransaction().rollback();
			} finally {
				status.getTransaction().close();
			}
		} finally {
			TransactionSynchronizationManager.unbindResource(EventstormTransactionStatus.class);
        }

		if (status.getPreviousTransaction() != null) {
			TransactionSynchronizationManager.bindResource(EventstormTransactionStatus.class, status);
		}
		
	}

	private TransactionStatus doBegin(TransactionDefinition definition, Transaction parent) {

		EventstormTransactionStatus status = null;
		
		if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_SUPPORTS) {
        	status = new EventstormTransactionStatus(this.transactionManager.newTransactionReadOnly(), definition, true, parent);
        } else if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED) {
	        	status = new EventstormTransactionStatus(this.transactionManager.newTransactionReadWrite(), definition, true, parent);
	    } else if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
        	status = new EventstormTransactionStatus(this.transactionManager.newTransactionIsolatedReadWrite(), definition, true, parent);
        }

		if (parent != null) {
			TransactionSynchronizationManager.unbindResource(EventstormTransactionStatus.class);
		}
		
        if (status != null) {
        	TransactionSynchronizationManager.bindResource(EventstormTransactionStatus.class, status);
        }
        
        return status;
    }
}

