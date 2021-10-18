package eu.eventstorm.saga.impl;

import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.core.id.UniversalUniqueIdentifier;
import eu.eventstorm.core.id.UniversalUniqueIdentifierV6;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.saga.SagaContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SagaContextImpl implements SagaContext {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final Command sagaCommand;
    private final UniversalUniqueIdentifier universalUniqueIdentifier;

    private final Map<String, Object> holder = new HashMap<>();
    private final List<CloudEvent> events = new ArrayList<>(8);

    public SagaContextImpl(Command sagaCommand) {
        this.sagaCommand = sagaCommand;
        this.universalUniqueIdentifier = UniversalUniqueIdentifierV6.from(Short.MAX_VALUE, Short.MAX_VALUE, COUNTER.incrementAndGet());
    }

    @Override
    public <E extends Command> E getSagaCommand() {
        return (E) this.sagaCommand;
    }

    @Override
    public UniversalUniqueIdentifier getUniversalUniqueIdentifier() {
        return this.universalUniqueIdentifier;
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
