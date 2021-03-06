package eu.eventstorm.sql.type;


import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface JsonList {

    <T> T get(int index, Class<T> clazz);

	<T> void add(T value);

    <T> T remove(int index, Class<T> clazz);

	int size();

	<T> List<T> copyOf();

}