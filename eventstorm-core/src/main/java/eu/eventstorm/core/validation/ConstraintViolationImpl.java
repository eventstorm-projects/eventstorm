package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ConstraintViolationImpl implements ConstraintViolation {

	private final ImmutableList<String> properties;
	
	private final String name;

	public ConstraintViolationImpl(ImmutableList<String> properties, String name) {
		this.properties = properties;
		this.name = name;
	}

	public ImmutableList<String> getProperties() {
		return properties;
	}

	public String getName() {
		return name;
	}
	
}
