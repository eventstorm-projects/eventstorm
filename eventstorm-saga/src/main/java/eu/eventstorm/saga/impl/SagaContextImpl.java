package eu.eventstorm.saga.impl;

import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.saga.SagaContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SagaContextImpl implements SagaContext {

    private final Map<String, Object> holder = new HashMap<>();

    private final Command sagaCommand;

    private final List<CloudEvent> events = new ArrayList<>(8);

    public SagaContextImpl(Command sagaCommand) {
        this.sagaCommand = sagaCommand;
    }

    @Override
    public <E extends Command> E getSagaCommand() {
        return (E) this.sagaCommand;
    }

    @Override
    public <T> T get(String id) {
        return (T) holder.get(id);
    }

    @Override
    public void put(String id, Object object) {
        holder.put(id, object);
    }

    @Override
    public void push(CloudEvent event) {
        this.events.add(event);
    }

    @Override
    public List<CloudEvent> getEvents() {
        return events;
    }

}
