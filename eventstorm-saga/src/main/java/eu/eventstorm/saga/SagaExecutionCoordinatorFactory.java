package eu.eventstorm.saga;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface SagaExecutionCoordinatorFactory {

    void register(SagaDefinition definition);

    SagaExecutionCoordinator newInstance(String identifier);
}
