package eu.eventstorm.sql.tx;

import java.sql.PreparedStatement;
import java.util.function.UnaryOperator;

public final class TransactionManagerConfiguration {
	
	static final TransactionManagerConfiguration DEFAULT = new TransactionManagerConfiguration(TransactionTracers.TRANSACTION_TRACER_LOGGER , UnaryOperator.identity());
	
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
