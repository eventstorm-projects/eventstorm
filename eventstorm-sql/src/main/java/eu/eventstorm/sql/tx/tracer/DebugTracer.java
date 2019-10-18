package eu.eventstorm.sql.tx.tracer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.tx.Transaction;

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
		public void exception(EventstormSqlException cause) {
			LOGGER.debug("exception()", cause);
        }
         @Override
		public void exception(SQLException cause) {
            LOGGER.debug("exception()", cause);
		}
		@Override
		public void close() {
			LOGGER.debug("close()");
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
	public TransactionSpan close() {
		LOGGER.debug("close()");
		return NO_OP_SPAN;
	}

	@Override
	public TransactionSpan span() {
		LOGGER.debug("span()");
		return NO_OP_SPAN;
	}

	@Override
	public PreparedStatement decorate(String sql, PreparedStatement prepareStatement) {
		LOGGER.debug("decorate({})->({})", prepareStatement, sql);
		return prepareStatement;
	}

}
