package eu.eventstorm.saga.memory;

import eu.eventstorm.saga.SagaContext;
import eu.eventstorm.saga.SagaDefinition;
import eu.eventstorm.saga.SagaExecutionCoordinator;
import eu.eventstorm.saga.SagaParticipant;
import eu.eventstorm.saga.SagaParticipantException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class InMemorySagaExecutionCoordinator implements SagaExecutionCoordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySagaExecutionCoordinator.class);

    private final SagaDefinition definition;

    public InMemorySagaExecutionCoordinator(SagaDefinition definition) {
        this.definition = definition;
    }

    @Override
    public Mono<SagaContext> execute(SagaContext context) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute [{}] with context [{}]", definition.getIdentifier(), context);
        }

        return Flux.fromIterable(definition.getParticipants())
                .index()
                .flatMapSequential(tuple -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("execute participant [{}]", tuple);
                    }
                    return tuple.getT2().execute(context)
                            .onErrorResume(Exception.class, t -> {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("exception on [" + tuple + "]", t);
                                }
                                return Mono.error(new SagaParticipantException(t, tuple, definition, context));
                            });
                }, 1)
                .onErrorResume(SagaParticipantException.class, ex -> Flux.fromIterable(definition.getParticipants().subList(0, (int) ex.getIndex()).reverse())
                        .flatMap(p -> p.compensate(context))
                        .collectList()
                        .flatMap(list -> Mono.error(ex)))
                .collectList()
                .flatMap(t -> Mono.just(context));
    }

}

