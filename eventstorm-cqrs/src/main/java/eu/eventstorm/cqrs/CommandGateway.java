package eu.eventstorm.cqrs;

import com.google.common.collect.ImmutableMap;

import brave.Span;
import brave.Tracer;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandGateway {

    private final CommandHandlerRegistry registry;
    
    private final Tracer tracer;
    
    public CommandGateway(CommandHandlerRegistry registry, Tracer tracer) {
        this.registry = registry;
        this.tracer = tracer;
    }

	public <T extends Command, E> Flux<E> dispatch(CommandContext ctx, T command) {
		CommandHandler<T,E> handler;
		Span span = this.tracer.nextSpan().name("dispatch");
	    try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(span.start())) {
	    	handler = registry.<T,E>get(command);
			// if no command handler => error
			if (handler == null) {
				throw new CommandGatewayException(CommandGatewayException.Type.NOT_FOUND, ImmutableMap.of("command", command));
			}
	    }
	    return handler.handle(ctx, command);
    }

}
