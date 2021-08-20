package eu.eventstorm.saga.memory;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.saga.SagaDefinition;
import eu.eventstorm.saga.SagaParticipant;

final class InMemorySagaDefinition implements SagaDefinition {

    private final String identifier;

    private final ImmutableList<SagaParticipant> participants;

    InMemorySagaDefinition(String identifier, ImmutableList<SagaParticipant> participants) {
        this.identifier = identifier;
        this.participants = participants;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    public ImmutableList<SagaParticipant> getParticipants() {
        return participants;
    }
}
