package eu.eventstorm.cqrs.validation;


import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public final class ReactiveValidators {

    private ReactiveValidators() {
    }

    private static final Function<CommandContext, Mono<CommandContext>> CHECK = ctx -> {
        if (ctx.hasConstraintViolation()) {
            return Mono.error(new CommandValidationException(ctx));
        }
        return Mono.just(ctx);
    };

    public static <T extends Command> ReactiveValidator empty() {
        return Mono::just;
    }

    public static <E extends Command> ReactiveValidator from(Validator<E> validator) {
        return ctx -> {
            try {
                validator.validate(ctx, ctx.getCommand());
            } catch (Exception exception) {
                return Mono.error(exception);
            }
            return Mono.just(ctx);
        };
    }

    public static Function<CommandContext, Mono<CommandContext>> check() {
        return CHECK;
    }


}
