package eu.eventstorm.sql.tx;

import java.sql.PreparedStatement;

import eu.eventstorm.sql.tx.tracer.TransactionSpan;

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
	public PreparedStatement getPreparedStatement() {
		return this.ps;
	}

}
