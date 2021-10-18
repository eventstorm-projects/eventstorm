package eu.eventstorm.saga;

import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.core.id.UniversalUniqueIdentifier;
import eu.eventstorm.cqrs.Command;

import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface SagaContext {

    <E extends Command> E getSagaCommand();

    UniversalUniqueIdentifier getUniversalUniqueIdentifier();

    <T> T get(String id);

    void put(String id, Object object);

    void push(CloudEvent event);

    List<CloudEvent> getEvents();
}
