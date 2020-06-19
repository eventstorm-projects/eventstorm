package eu.eventstorm.sql.impl;

import java.sql.PreparedStatement;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.tracer.TransactionSpan;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionQueryContextImpl implements TransactionQueryContext {

	private final TransactionSpan span;
	private final PreparedStatement ps;

	public TransactionQueryContextImpl(PreparedStatement ps, SqlQuery query, TransactionManagerConfiguration configuration) {
		this.span = configuration.getTracer().span("query");
		this.span.tag("sql", query.sql());
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

	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("transactionSpan", span)
				.append("preparedStatement", ps)
				.toString();
	}
	
	

}
