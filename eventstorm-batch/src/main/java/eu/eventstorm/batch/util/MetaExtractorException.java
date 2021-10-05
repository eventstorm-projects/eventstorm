package eu.eventstorm.batch.util;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class MetaExtractorException extends EventstormException{

	public enum Type implements EventstormExceptionType {
		HEADER_NOT_FOUND, FAILED_TO_PARSE, QUERY_PARAM_NOT_FOUND
	}
	
	public MetaExtractorException(EventstormExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
		super(type, values, cause);
	}
	public MetaExtractorException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

}
