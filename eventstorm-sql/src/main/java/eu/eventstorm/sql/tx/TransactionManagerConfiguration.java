package eu.eventstorm.sql.tx;

import java.sql.PreparedStatement;
import java.util.function.UnaryOperator;

import eu.eventstorm.sql.tx.tracer.TransactionTracer;
import eu.eventstorm.sql.tx.tracer.TransactionTracers;

public final class TransactionManagerConfiguration {
	
	protected static final TransactionManagerConfiguration DEFAULT = new TransactionManagerConfiguration(TransactionTracers.noOp(), ps -> ps);
	
	private final TransactionTracer tracer;
	
	private final UnaryOperator<PreparedStatement> decorator;

	public TransactionManagerConfiguration(TransactionTracer tracer, UnaryOperator<PreparedStatement> decorator) {
		this.tracer = tracer;
		this.decorator = decorator;
	}

	public TransactionTracer getTracer() {
		return this.tracer;
	}

	public PreparedStatement preparedStatement(PreparedStatement prepareStatement) {
		return decorator.apply(prepareStatement);
	}
	
	
}
