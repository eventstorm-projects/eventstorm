package eu.eventstorm.cqrs;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface QueryDescriptors {

	SqlQueryDescriptor getSqlQueryDescriptor(String fcqn);

}
