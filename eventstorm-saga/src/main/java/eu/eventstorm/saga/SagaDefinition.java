package eu.eventstorm.saga;

import com.google.common.collect.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface SagaDefinition {

    String getIdentifier();

    ImmutableList<SagaParticipant> getParticipants();

}
