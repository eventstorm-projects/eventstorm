package eu.eventstorm.saga.memory;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.saga.SagaDefinition;
import eu.eventstorm.saga.SagaParticipant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemorySagaDefinitionBuilder {

    private String identifier;
    private List<SagaParticipant> participants = new ArrayList<>();


    public SagaDefinition build() {
        return new InMemorySagaDefinition(identifier, ImmutableList.copyOf(participants));
    }

    public InMemorySagaDefinitionBuilder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public InMemorySagaDefinitionBuilder withParticipant(SagaParticipant participant) {
        this.participants.add(participant);
        return this;
    }
}
