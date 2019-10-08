package eu.eventstorm.sql.type;

public interface Json {

	<T> T get(String key, Class<T> clazz);

	void put(String key, Object value);

	
	void flush();
}