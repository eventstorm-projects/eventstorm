package eu.eventstorm.saga;

import reactor.core.publisher.Mono;

public interface SagaParticipant {

    Mono<SagaContext> execute(SagaContext context);

    Mono<SagaContext> compensate(SagaContext context);

}
