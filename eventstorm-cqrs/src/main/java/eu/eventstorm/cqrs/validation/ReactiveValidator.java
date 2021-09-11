package eu.eventstorm.cqrs.validation;

import eu.eventstorm.cqrs.CommandContext;
import reactor.core.publisher.Mono;

public interface ReactiveValidator {

    Mono<CommandContext> validate(CommandContext ctx);

}
