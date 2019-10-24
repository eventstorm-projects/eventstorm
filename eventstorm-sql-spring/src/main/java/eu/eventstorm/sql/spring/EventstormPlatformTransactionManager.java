package eu.eventstorm.sql.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import eu.eventstorm.sql.tx.TransactionManager;

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
    public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getTransaction({})", transactionDefinition);
        }

        if (transactionManager.hasCurrent()) {
            // get TX inside another TX
            return new EventstormTransactionStatus(transactionManager.current(), transactionDefinition.isReadOnly(), false, null);
        }

        if (transactionDefinition.isReadOnly()) {
            return new EventstormTransactionStatus(transactionManager.newTransactionReadOnly(), true, true, null);
        } else {
            return new EventstormTransactionStatus(transactionManager.newTransactionReadWrite(), false, true, null);
        }

    }

    @Override
    public void commit(TransactionStatus transactionStatus) throws TransactionException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("commit({})", transactionStatus);
        }

        EventstormTransactionStatus status = (EventstormTransactionStatus)transactionStatus;

		try {
			status.getTransaction().commit();
		} finally {
			status.getTransaction().close();
		}


    }

    @Override
    public void rollback(TransactionStatus transactionStatus) throws TransactionException {
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

		EventstormTransactionStatus status = (EventstormTransactionStatus)transactionStatus;

		try {
			status.getTransaction().rollback();
		} finally {
			status.getTransaction().close();
		}

		//if (((EventstormTransactionStatus)transactionStatus).getPreviousTransaction() != null) {
		//	TransactionSynchronizer.bind(this.name, ((EventstormTransactionStatus)transactionStatus).getPreviousTransaction());
		//}
    }
}
