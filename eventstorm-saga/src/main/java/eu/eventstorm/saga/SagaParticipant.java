package eu.eventstorm.saga;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface SagaParticipant {

    Mono<SagaContext> execute(SagaContext context);

    Mono<SagaContext> compensate(SagaContext context);

}
