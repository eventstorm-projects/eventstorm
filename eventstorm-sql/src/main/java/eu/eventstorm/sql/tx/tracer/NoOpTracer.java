package eu.eventstorm.sql.tx.tracer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.tx.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class NoOpTracer implements TransactionTracer {

	private final static TransactionSpan NO_OP_SPAN = new TransactionSpan() {
		@Override
		public void tag(String key, String value) {
			 // Do nothing because no tracing op
		}
		@Override
		public void exception(EventstormSqlException cause) {
			// Do nothing because no tracing op
		}
        @Override
		public void exception(SQLException cause) {
        	// Do nothing because no tracing op
		}
        @Override
		public void close() {
        	// Do nothing because no tracing op
		}
		@Override
		public void annotate(String annotation) {
			// Do nothing because no tracing op
		}
	};

	@Override
	public TransactionSpan begin(Transaction transaction) {
		return NO_OP_SPAN;
	}

	@Override
	public TransactionSpan span(String name) {
		return NO_OP_SPAN;
	}

	@Override
	public PreparedStatement decorate( PreparedStatement prepareStatement) {
		return prepareStatement;
	}


}
