package eu.eventstorm.core.validation;

public interface PropertyValidator<T> {

	void validate(String property, T value, ValidatorContext validatorContext);
	
}