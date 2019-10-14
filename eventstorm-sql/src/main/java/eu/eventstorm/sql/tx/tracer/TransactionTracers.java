package eu.eventstorm.sql.tx.tracer;

public final class TransactionTracers {

	private TransactionTracers() {
	}
	
	public static TransactionTracer noOp() {
		return new NoOpTracer();
	}
	
	/*public static final TransactionSpan TRANSACTION_SPAN_LOGGER = new TransactionSpan() {
		
		@Override
		public void tag(String key, String value) {
			LOGGER.debug("tag({},{})",key,value);
		}
		
		@Override
		public void exception(SQLException cause) {
			LOGGER.debug("exception()", cause);
		}
		
		@Override
		public void close() {
			LOGGER.debug("close()");
		}
	}; 
	public static final TransactionTracer TRANSACTION_TRACER_LOGGER = new TransactionTracer() {
		
		@Override
		public TransactionSpan span() {
			LOGGER_TRACER.debug("span()");
			return TRANSACTION_SPAN_LOGGER;
		}
		
		@Override
		public TransactionSpan rollback() {
			LOGGER_TRACER.debug("rollback()");
			return TRANSACTION_SPAN_LOGGER;
		}
		
		@Override
		public TransactionSpan commit() {
			LOGGER_TRACER.debug("commit()");
			return TRANSACTION_SPAN_LOGGER;
		}
		
		@Override
		public TransactionSpan close() {
			LOGGER_TRACER.debug("close()");
			return TRANSACTION_SPAN_LOGGER;
		}
	};*/
}
