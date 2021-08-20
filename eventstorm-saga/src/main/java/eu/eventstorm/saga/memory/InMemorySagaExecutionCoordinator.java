package eu.eventstorm.saga.memory;

import eu.eventstorm.saga.SagaContext;
import eu.eventstorm.saga.SagaDefinition;
import eu.eventstorm.saga.SagaExecutionCoordinator;
import eu.eventstorm.saga.SagaMessage;
import eu.eventstorm.saga.SagaParticipant;
import eu.eventstorm.saga.util.CountingLatch;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.CoreSubscriber;

import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class InMemorySagaExecutionCoordinator implements SagaExecutionCoordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySagaExecutionCoordinator.class);

    private final SagaDefinition definition;
    private final String uuid;
    private final String traceId;

    public InMemorySagaExecutionCoordinator(SagaDefinition definition) {
        this.definition = definition;
        this.traceId = "";
        this.uuid = "";
    }


    @Override
    public void execute(SagaContext context) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute [{}] with context [{}]", definition.getIdentifier(), context);
        }

        ListIterator<SagaParticipant> participants = definition.getParticipants().listIterator();
        //  SagaLog log = null;

        CountingLatch latch = new CountingLatch(definition.getParticipants().size());

        try {
            // log.start();

            try {
                participants.next()
                        .execute(context)
                        .subscribe(new SagaCoreSubscriber(latch, context, participants));

                latch.await(60, TimeUnit.SECONDS);

                LOGGER.info("FINISH COORDINATION -> " + latch.getCount());

            } catch (Exception cause) {
                cause.printStackTrace();
            }


        } finally {
            //log.stop();
        }
        LOGGER.info("FINISH COORDINATION 1 -> " + +latch.getCount());
    }


    private static class SagaCoreSubscriber implements CoreSubscriber<SagaMessage> {

        private final CountingLatch latch;
        private final ListIterator<SagaParticipant> participants;
        private final SagaContext context;

        public SagaCoreSubscriber(CountingLatch latch, SagaContext context, ListIterator<SagaParticipant> participants) {
            this.latch = latch;
            this.context = context;
            this.participants = participants;
        }

        @Override
        public void onSubscribe(Subscription s) {
            s.request(1);
        }

        @Override
        public void onNext(SagaMessage sagaMessage) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("onNext({},{})", context, sagaMessage);
            }

            // previous ok -> decrement
            latch.decrement();

            context.push(sagaMessage);

            if (participants.hasNext()) {
                participants.next().execute(context).subscribe(this);
            }

        }

        @Override
        public void onError(Throwable t) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("onError({})", context, t);
            }


            latch.resetTo((participants.previousIndex() + 1));


            while (participants.hasPrevious()) {
                participants.previous().compensate(context).subscribe(new Subscriber<SagaMessage>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);
                    }

                    @Override
                    public void onNext(SagaMessage sagaMessage) {
                        LOGGER.debug("onNext({})", context);
                    }

                    @Override
                    public void onError(Throwable t) {
                        LOGGER.debug("onError({})", context);
                        latch.decrement();
                    }

                    @Override
                    public void onComplete() {

                        LOGGER.debug("onComplete({})->{}", context, latch.getCount());
                        latch.decrement();
                    }
                });
            }

        }

        @Override

        public void onComplete() {
//
        }
    }
}
