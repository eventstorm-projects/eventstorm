package eu.eventstorm.saga;

import eu.eventstorm.saga.impl.SagaContextImpl;
import eu.eventstorm.saga.memory.InMemorySagaDefinitionBuilder;
import eu.eventstorm.saga.memory.InMemorySagaExecutionCoordinatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicInteger;

class SimpleTest {

    @Test
    void localTest() {

        SagaParticipant participant_1 = new LogParticipant("part 1");
        SagaParticipant participant_2 = new LogParticipant("part 2");


        SagaDefinition definition = new InMemorySagaDefinitionBuilder()
                .withIdentifier("createOrder")
                //  .withParticipant(participant_1)
                //  .withParticipant(new ErrorParticipant())
                //  .withParticipant(participant_2)
                .withParticipant(new CounterParticipant())
                .withParticipant(new ErrorParticipant())
             //   .withParticipant(new CounterParticipant())
             //   .withParticipant(new CounterParticipant())
                .build();

        SagaExecutionCoordinatorFactory factory = new InMemorySagaExecutionCoordinatorFactory();
        factory.register(definition);

        SagaExecutionCoordinator coordinator = factory.newInstance("createOrder");

        SagaContext context = new SagaContextImpl();
        context.put("counter", new AtomicInteger(10));
        coordinator.execute(context);

        LOGGER.info("FINISH");

        AtomicInteger counter = context.get("counter");
        Assertions.assertEquals(10, counter.get());
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTest.class);

    private static final class CounterParticipant implements SagaParticipant {

        @Override
        public Publisher<SagaMessage> execute(SagaContext context) {
            return Mono.just(context)
                    .publishOn(Schedulers.boundedElastic())
                    .map(ctx -> {
                        AtomicInteger counter = ctx.get("counter");
                        counter.set(counter.get() + 10);
                        return new SagaMessage();
                    });
        }

        @Override
        public Publisher<SagaMessage> compensate(SagaContext context) {
            LOGGER.info("compensate({}) ", context);
            return Mono.just(context)
                    .publishOn(Schedulers.parallel())
                    .map(ctx -> {
                        AtomicInteger counter = ctx.get("counter");
                        counter.set(counter.get() - 10);
                        return new SagaMessage();
                    });
        }
    }

    private static final class LogParticipant implements SagaParticipant {

        private final String name;

        public LogParticipant(String name) {
            this.name = name;
        }

        @Override
        public Publisher<SagaMessage> execute(SagaContext context) {
            LOGGER.info("execute({}) for {}", context, this.name);
            return Mono.just(new SagaMessage());
        }

        @Override
        public Publisher<SagaMessage> compensate(SagaContext context) {
            LOGGER.info("compensate 01");
            return Mono.just(new SagaMessage());
        }
    }

    private static final class ErrorParticipant implements SagaParticipant {

        @Override
        public Publisher<SagaMessage> execute(SagaContext context) {
            LOGGER.info("execute({}) -> mono.error", context);
            return Mono.error(new IllegalStateException());
        }

        @Override
        public Publisher<SagaMessage> compensate(SagaContext context) {
            LOGGER.info("compensate() ");
            return Mono.just(new SagaMessage());
        }
    }
}
