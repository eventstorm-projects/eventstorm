package eu.eventstorm.saga.impl;

import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.cloudevents.CloudEvents;
import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.context.ReactiveCommandContext;
import eu.eventstorm.saga.SagaContext;
import eu.eventstorm.saga.SagaParticipant;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class CommandGatewayParticipant<T extends Command> implements SagaParticipant {

    private static final String SERVER_WEB_EXCHANGE = ServerWebExchange.class.getName();

    private final CommandGateway gateway;

    public CommandGatewayParticipant(CommandGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Mono<SagaContext> compensate(SagaContext context) {
        return Mono.just(context);
    }

    protected abstract T getCommand(SagaContext context);

    protected final Mono<List<CloudEvent>> executeHandler(T command, SagaContext context) {
        ReactiveCommandContext commandContext = new ReactiveCommandContext(command, context.get(SERVER_WEB_EXCHANGE));
        return gateway.<Event>dispatch(commandContext)
                .map(CloudEvents::to)
                .collectList()
                .map(events -> {
                    events.forEach(context::push);
                    return events;
                });
    }
}