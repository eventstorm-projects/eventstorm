package eu.eventstorm.saga.memory;

import eu.eventstorm.saga.SagaDefinition;
import eu.eventstorm.saga.SagaExecutionCoordinator;
import eu.eventstorm.saga.SagaInstance;
import eu.eventstorm.saga.SagaExecutionCoordinatorFactory;

import java.util.HashMap;
import java.util.Map;

public final class InMemorySagaExecutionCoordinatorFactory implements SagaExecutionCoordinatorFactory {

    private final Map<String, SagaDefinition> definitions = new HashMap<>();
    @Override
    public void register(SagaDefinition definition) {
        definitions.put(definition.getIdentifier(), definition);
    }

    @Override
    public SagaExecutionCoordinator newInstance(String identifier) {
        return new InMemorySagaExecutionCoordinator(definitions.get(identifier));
    }

}