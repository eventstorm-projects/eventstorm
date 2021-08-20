package eu.eventstorm.saga;

import java.util.concurrent.atomic.AtomicInteger;

public interface SagaContext {

    void push(SagaMessage sagaMessage);

    <T> T get(String id);

    void put(String id, Object object);
}
