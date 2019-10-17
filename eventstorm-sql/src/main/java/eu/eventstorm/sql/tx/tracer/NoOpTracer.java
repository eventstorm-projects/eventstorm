package eu.eventstorm.sql.tx.tracer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.eventstorm.sql.tx.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class NoOpTracer implements TransactionTracer {

	private final static TransactionSpan NO_OP_SPAN = new TransactionSpan() {
		@Override
		public void tag(String key, String value) {
		}
		@Override
		public void exception(SQLException cause) {
		}
		@Override
		public void close() {
		}
	};
	
	@Override
	public TransactionSpan begin(Transaction transaction) {
		return NO_OP_SPAN;
	}

	@Override
	public TransactionSpan close() {
		return NO_OP_SPAN;
	}

	@Override
	public TransactionSpan span() {
		return NO_OP_SPAN;
	}

	@Override
	public PreparedStatement decorate(String sql, PreparedStatement prepareStatement) {
		return prepareStatement;
	}


}
