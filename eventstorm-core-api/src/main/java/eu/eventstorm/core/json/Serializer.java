package eu.eventstorm.core.json;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Serializer<T> {

	byte[] serialize(T object);

}
