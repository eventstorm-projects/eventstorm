package eu.eventstorm.sql.tx.tracer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zipkin2.Span;
import zipkin2.reporter.Reporter;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class LoggingBraveReporter implements Reporter<Span> {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingBraveReporter.class);
	
	@Override
	public void report(Span span) {
		LOGGER.info("{}", span);
	}

}
