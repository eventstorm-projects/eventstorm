package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.validation.ConstraintViolation;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ConstraintViolationImpl implements ConstraintViolation {

	private final ImmutableList<String> properties;
	
	private final String cause;

	public ConstraintViolationImpl(ImmutableList<String> properties, String cause) {
		this.properties = properties;
		this.cause = cause;
	}

	public ImmutableList<String> getProperties() {
		return properties;
	}

    public String getCause() {
        return cause;
    }
	
}
