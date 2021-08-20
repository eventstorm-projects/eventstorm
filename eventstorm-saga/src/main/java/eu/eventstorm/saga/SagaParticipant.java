package eu.eventstorm.saga;

import org.reactivestreams.Publisher;

public interface SagaParticipant {

    Publisher<SagaMessage> execute(SagaContext context);

    Publisher<SagaMessage> compensate(SagaContext context);
}
