package eu.eventstorm.sql.tracer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.impl.TransactionQueryContext;
import eu.eventstorm.sql.impl.TransactionQueryContextImpl;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class NoOpTracer implements TransactionTracer {

	private static final TransactionSpan NO_OP_SPAN = new TransactionSpan() {
		@Override
		public void tag(String key, String value) {
			 // Do nothing because no tracing op
		}
		@Override
		public void exception(Exception cause) {
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

	@Override
	public TransactionQueryContext newTransactionContext(PreparedStatement ps, SqlQuery query) {
		return new TransactionQueryContextImpl(ps);
	}


}
