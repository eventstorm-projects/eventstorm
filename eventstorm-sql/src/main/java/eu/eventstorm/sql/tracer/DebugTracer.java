package eu.eventstorm.sql.tracer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.impl.TransactionQueryContext;
import eu.eventstorm.sql.impl.TransactionQueryContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DebugTracer implements TransactionTracer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DebugTracer.class);

	private static final TransactionSpan NO_OP_SPAN = new TransactionSpan() {
		@Override
		public void tag(String key, String value) {
			LOGGER.debug("tag({},{})", key, value);
		}
		@Override
		public void exception(Exception cause) {
			LOGGER.debug("exception()", cause);
        }
    	@Override
		public void close() {
			LOGGER.debug("close()");
		}
		@Override
		public void annotate(String annotation) {
			LOGGER.debug("annotate({})", annotation);
		}
	};

	@Override
	public TransactionSpan begin(Transaction transaction) {
		LOGGER.debug("begin({})", transaction);
		return NO_OP_SPAN;
	}

	public void rollback(Transaction transaction) {
		LOGGER.debug("rollback({})", transaction);
	}

	public void commit(Transaction transaction) {
		LOGGER.debug("commit({})", transaction);
	}

	@Override
	public TransactionSpan span(String name) {
		LOGGER.debug("span({})", name);
		return NO_OP_SPAN;
	}
	
	@Override
	public PreparedStatement decorate( PreparedStatement prepareStatement) {
		LOGGER.debug("decorate({})", prepareStatement);
		return prepareStatement;
	}

	@Override
	public TransactionQueryContext newTransactionContext(PreparedStatement ps, SqlQuery query) {
		LOGGER.debug("newTransactionContext({},{}})", ps, query);
		return new TransactionQueryContextImpl(ps);
	}

}
