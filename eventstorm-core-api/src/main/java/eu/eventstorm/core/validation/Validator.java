package eu.eventstorm.core.validation;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Validator<T> {

	void validate(ValidatorContext context, T object);

}
