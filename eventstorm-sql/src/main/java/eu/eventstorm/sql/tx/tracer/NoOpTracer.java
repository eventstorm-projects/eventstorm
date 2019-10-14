package eu.eventstorm.sql.tx.tracer;

import java.sql.SQLException;

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
	public TransactionSpan rollback() {
		return NO_OP_SPAN;
	}

	@Override
	public TransactionSpan commit() {
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

}
