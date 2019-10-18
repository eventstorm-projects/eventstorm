package eu.eventstorm.sql.tx;

import java.sql.PreparedStatement;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.tx.tracer.TransactionSpan;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionQueryContextImpl implements TransactionQueryContext {

	private final TransactionSpan span;
	private final PreparedStatement ps;

	public TransactionQueryContextImpl(PreparedStatement ps, String sql, TransactionManagerConfiguration configuration) {
		this.span = configuration.getTracer().span();
		this.span.tag("sql", sql);
		this.ps = ps;
	}

	@Override
	public void close() {
		this.span.close();
	}

	@Override
	public PreparedStatement preparedStatement() {
		return this.ps;
	}

	@Override
	public <T extends EventstormSqlException> T exception(T cause) {
        span.exception(cause);
		return cause;
	}

}
