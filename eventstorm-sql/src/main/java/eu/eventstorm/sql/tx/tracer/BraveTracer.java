package eu.eventstorm.sql.tx.tracer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import brave.ScopedSpan;
import brave.Tracer;
import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.tx.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class BraveTracer implements TransactionTracer {

	private final Tracer tracer;

	BraveTracer(Tracer tracer) {
		this.tracer = tracer;
	}

	private static class BraveTransactionSpan implements TransactionSpan {

		private final ScopedSpan scopedSpan;
		public BraveTransactionSpan(ScopedSpan scopedSpan) {
			this.scopedSpan = scopedSpan;
		}

		@Override
		public void close() {
			this.scopedSpan.finish();
		}

		@Override
		public void exception(EventstormSqlException cause) {
			this.scopedSpan.error(cause);
		}

        @Override
		public void exception(SQLException cause) {
			this.scopedSpan.error(cause);
        }

		@Override
		public void tag(String key, String value) {
			this.scopedSpan.tag(key, value);
		}



	}

	@Override
	public TransactionSpan close() {
		return new BraveTransactionSpan(tracer.startScopedSpan("close"));
	}

	@Override
	public TransactionSpan span() {
		return new BraveTransactionSpan(tracer.startScopedSpan("span"));
	}

	@Override
	public PreparedStatement decorate(String sql, PreparedStatement prepareStatement) {
		this.tracer.currentSpan().tag("sql", sql);
		return new EventstormPreparedStatement(prepareStatement, this);
	}

	@Override
	public TransactionSpan begin(Transaction transaction) {
		return new BraveTransactionSpan(tracer.startScopedSpan("transaction"));

	}

}
