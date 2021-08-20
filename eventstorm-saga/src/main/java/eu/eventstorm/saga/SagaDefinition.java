package eu.eventstorm.saga;

import com.google.common.collect.ImmutableList;

public interface SagaDefinition {
    String getIdentifier();
    ImmutableList<SagaParticipant> getParticipants();
}
