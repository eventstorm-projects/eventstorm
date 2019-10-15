package eu.eventstorm.sql.tx.tracer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		public void exception(SQLException cause) {
			LOGGER.debug("exception()", cause);
		}
		@Override
		public void close() {
			LOGGER.debug("close()");
		}
	};
	@Override
	public TransactionSpan rollback() {
		LOGGER.debug("rollback()");
		return NO_OP_SPAN;
	}

	@Override
	public TransactionSpan commit() {
		LOGGER.debug("commit()");
		return NO_OP_SPAN;
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
	public PreparedStatement decorate(PreparedStatement prepareStatement) {
		LOGGER.debug("decorate({})", prepareStatement);
		return prepareStatement;
	}

}
