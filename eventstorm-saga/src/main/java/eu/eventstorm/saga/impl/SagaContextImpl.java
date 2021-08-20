package eu.eventstorm.saga.impl;

import eu.eventstorm.saga.SagaContext;
import eu.eventstorm.saga.SagaMessage;

import java.util.HashMap;
import java.util.Map;

public final class SagaContextImpl implements SagaContext {

    private final Map<String, Object> holder = new HashMap<>();

    @Override
    public void push(SagaMessage sagaMessage) {

    }

    @Override
    public <T> T get(String id) {
        return (T) holder.get(id);
    }

    @Override
    public void put(String id, Object object) {
        holder.put(id, object);
    }

}
