package eu.eventstorm.core;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface DomainHandler<T extends DomainModel> {

	void on(T domainModel);
}
