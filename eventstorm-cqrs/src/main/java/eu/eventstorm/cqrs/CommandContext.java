package eu.eventstorm.cqrs;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CommandContext {

	<T> void put(String key, T value);
	
	<T> T get(String key);
	
}
