package eu.eventstorm.sql.tx.tracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionTracers {

	private TransactionTracers() {
	}
	
	public static TransactionTracer noOp() {
		return new NoOpTracer();
	}
	
	public static TransactionTracer debug() {
		return new DebugTracer();
	}
}
