package eu.eventstorm.saga;

public interface SagaExecutionCoordinatorFactory {

    void register(SagaDefinition definition);

    SagaExecutionCoordinator newInstance(String identifier);
}
