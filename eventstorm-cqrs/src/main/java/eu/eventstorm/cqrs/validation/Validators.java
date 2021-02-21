package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.cqrs.Command;

import java.util.function.Supplier;

import static com.google.common.collect.ImmutableList.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Validators {

	private static final Validator<Command> EMTPY = (context, command) -> ImmutableList.of();

	private Validators() {
	}

	@SuppressWarnings("unchecked")
	public static <T extends Command> Validator<T> empty() {
		return (Validator<T>) EMTPY;
	}

	public static <T extends Command> ComposeValidator<T> compose() {
		return new ComposeValidator<>();
	}

	
	public static final class ComposeValidator<T> {
		
		private ImmutableList<ConstraintViolation> result = of();
		
		private ComposeValidator() {
		}

		public ComposeValidator<T> and(Supplier<ImmutableList<ConstraintViolation>> supplier) {
			if (result.isEmpty()) {
				result = supplier.get();
			}
			return this;
		}
		
		public ImmutableList<ConstraintViolation> evaluate() {
			return result;
		}		
	}
}
