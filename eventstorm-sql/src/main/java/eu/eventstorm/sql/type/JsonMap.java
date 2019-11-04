package eu.eventstorm.sql.type;

import java.util.Set;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface JsonMap {

	Set<String> keys();

	<T> T get(String key, Class<T> clazz);

	void put(String key, Object value);

    Object remove(String key);

}