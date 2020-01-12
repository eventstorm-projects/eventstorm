package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Command;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Validators {
	
	private static final Validator<Command> EMTPY = command -> ImmutableList.of();
	
	private Validators() {
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Command> Validator<T> empty() {
		return (Validator<T>) EMTPY;
	}
	
}
