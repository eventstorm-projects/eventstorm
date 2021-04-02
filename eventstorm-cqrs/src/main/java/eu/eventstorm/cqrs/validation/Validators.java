package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.core.validation.ValidatorContext;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.util.tuple.Tuple2;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Validators {

	private static final Validator<Command> EMPTY = (context, command) -> {
	};

	private Validators() {
	}

	public static <T extends Command> Validator<T> empty() {
		return (Validator<T>) EMPTY;
	}

	public static <T extends Command> Validator<T> from(ImmutableList<BiConsumer<CommandContext,T>> rules) {
		return (context, object) -> {
			CommandContext ctx = (CommandContext) context;
			for (BiConsumer<CommandContext,T> rule : rules) {
				rule.accept(ctx, object);
				if (ctx.hasConstraintViolation()) {
					return;
				}
			}
		};
	}

	public static <T extends Command> ComposeValidator<T> compose(ValidatorContext context) {
		return new ComposeValidator<>(context);
	}

	public static final class ComposeValidator<T> {
		
		private final ValidatorContext validatorContext;
		
		private ComposeValidator(ValidatorContext validatorContext) {
			this.validatorContext = validatorContext;
		}


		public ComposeValidator<T> and(Consumer<ValidatorContext> rule) {
			if (!validatorContext.hasConstraintViolation()) {
				rule.accept(validatorContext);
			}
			return this;
		}

	}
}
