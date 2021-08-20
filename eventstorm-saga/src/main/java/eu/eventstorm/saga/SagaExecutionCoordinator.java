package eu.eventstorm.saga;

/**
 * The Saga Execution Coordinator (SEC) is the core component for implementing a successful Saga flow.
 * It maintains a Saga log that contains the sequence of events of a particular flow.
 * If a failure occurs within any of the components, the SEC queries the logs and helps identify which components are
 * impacted and in which sequence the compensating transactions must be executed.
 * Essentially, the SEC helps maintain an eventually consistent state of the overall process.
 */
public interface SagaExecutionCoordinator {

    void execute(SagaContext context);

}
