package eu.eventstorm.sql.type;


import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface JsonList<T> {

    T get(int index);

	void add(T value);

    T remove(int index);

	int size();

	List<T> copyOf();

}