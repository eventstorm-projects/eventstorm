package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

public interface PropertyValidator<T> {

	void validate(ImmutableList<String> properties, T value, ImmutableList.Builder<ConstraintViolation> builder);
	
}