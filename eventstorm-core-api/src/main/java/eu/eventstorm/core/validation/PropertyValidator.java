package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

public interface PropertyValidator<T> {

	void validate(String property, T value, ImmutableList.Builder<ConstraintViolation> builder);
	
}