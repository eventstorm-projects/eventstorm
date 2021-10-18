package eu.eventstorm.saga;

import reactor.util.function.Tuple2;

public final class SagaParticipantException extends RuntimeException {

    private final long index;
    private final SagaParticipant participant;
    private final SagaDefinition definition;
    private final SagaContext context;

    public SagaParticipantException(Exception cause, Tuple2<Long, SagaParticipant> tuple, SagaDefinition definition, SagaContext context) {
        super(cause);
        this.index = tuple.getT1();
        this.participant = tuple.getT2();
        this.definition = definition;
        this.context = context;
    }

    public long getIndex() {
        return index;
    }

    public SagaParticipant getParticipant() {
        return participant;
    }

    public SagaDefinition getDefinition() {
        return definition;
    }

    public SagaContext getContext() {
        return context;
    }

}
