package eu.eventstorm.core.json;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Deserializer<T> {

	T deserialize(byte[] bytes);
	
}
