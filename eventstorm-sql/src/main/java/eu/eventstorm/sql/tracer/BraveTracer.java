package eu.eventstorm.sql.tracer;

import brave.ScopedSpan;
import brave.Tracer;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.impl.TransactionQueryContext;
import eu.eventstorm.sql.impl.TraceTransactionQueryContextImpl;

import java.sql.PreparedStatement;

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
		public void exception(Exception cause) {
			this.scopedSpan.error(cause);
		}

		@Override
		public void tag(String key, String value) {
			this.scopedSpan.tag(key, value);
		}

		@Override
		public void annotate(String annotation) {
			this.scopedSpan.annotate(annotation);
		}

	}

	@Override
	public TransactionSpan span(String name) {
		return new BraveTransactionSpan(tracer.startScopedSpan(name));
	}

	@Override
	public PreparedStatement decorate(PreparedStatement prepareStatement) {
		return new BravePreparedStatement(prepareStatement, this);
	}

	@Override
	public TransactionQueryContext newTransactionContext(PreparedStatement ps, SqlQuery query) {
		return new TraceTransactionQueryContextImpl(ps, query, this);
	}

	@Override
	public TransactionSpan begin(Transaction transaction) {
		return new BraveTransactionSpan(tracer.startScopedSpan("transaction"));
	}

	Tracer getTracer() {
		return this.tracer;
	}
}
