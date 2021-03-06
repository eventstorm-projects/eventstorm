package eu.eventstorm.sql.impl;

import java.sql.PreparedStatement;

import eu.eventstorm.sql.tracer.TransactionTracer;
import eu.eventstorm.sql.tracer.TransactionTracers;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionManagerConfiguration {
	
	protected static final TransactionManagerConfiguration DEFAULT = new TransactionManagerConfiguration(TransactionTracers.noOp());
	
	private final TransactionTracer tracer;
	
	public TransactionManagerConfiguration(TransactionTracer tracer) {
		this.tracer = tracer;
	}

	public TransactionTracer getTracer() {
		return this.tracer;
	}

	public PreparedStatement preparedStatement(PreparedStatement prepareStatement) {
		return tracer.decorate(prepareStatement);
	}

	
}
