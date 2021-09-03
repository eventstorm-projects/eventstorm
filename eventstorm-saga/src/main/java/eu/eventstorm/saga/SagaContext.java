package eu.eventstorm.saga;

import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.cqrs.Command;

import java.util.List;

public interface SagaContext {

    <E extends Command> E getSagaCommand();

    <T> T get(String id);

    void put(String id, Object object);

    void push(CloudEvent event);

    List<CloudEvent> getEvents();
}
