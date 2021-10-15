package eu.eventstorm.saga.memory;

import eu.eventstorm.core.id.UniversalUniqueIdentifier;
import eu.eventstorm.core.id.UniversalUniqueIdentifierV6;
import eu.eventstorm.saga.SagaContext;
import eu.eventstorm.saga.SagaDefinition;
import eu.eventstorm.saga.SagaExecutionCoordinator;
import eu.eventstorm.saga.SagaParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public class InMemorySagaExecutionCoordinator implements SagaExecutionCoordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySagaExecutionCoordinator.class);

    private final SagaDefinition definition;
    private final String uuid;

    public InMemorySagaExecutionCoordinator(SagaDefinition definition) {
        UniversalUniqueIdentifier universalUniqueIdentifier = UniversalUniqueIdentifierV6.from(Short.MAX_VALUE, Short.MAX_VALUE, 0);
        this.definition = definition;
        this.uuid = universalUniqueIdentifier.toString();
    }

    @Override
    public Mono<SagaContext> execute(SagaContext context) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute [{}] with context [{}]", definition.getIdentifier(), context);
        }

        return Flux.fromIterable(definition.getParticipants())
                .index()
                .flatMapSequential(tuple ->  {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("execute participant [{}]", tuple);
                    }
                    return tuple.getT2().execute(context)
                            .onErrorResume(Exception.class, t -> {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("exception on [" + tuple +"]", t);
                                }
                                return Mono.error(new SagaParticipantException(t, tuple));
                            });
                }, 1)
                .onErrorResume(SagaParticipantException.class, ex -> Flux.fromIterable(definition.getParticipants().subList(0, (int) ex.index).reverse())
                        .flatMap(p -> p.compensate(context))
                        .collectList()
                        .flatMap(list -> Mono.just(context)))
                .collectList()
                .flatMap(t -> Mono.just(context));
    }

    private static class SagaParticipantException extends RuntimeException {

        private final long index;
        private final SagaParticipant participant;

        public SagaParticipantException(Exception cause, Tuple2<Long, SagaParticipant> tuple) {
            super(cause);
            this.index = tuple.getT1();
            this.participant = tuple.getT2();
        }
    }

}

