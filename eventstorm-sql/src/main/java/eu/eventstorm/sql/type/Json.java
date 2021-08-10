package eu.eventstorm.sql.type;

import eu.eventstorm.sql.JsonMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Json {

    JsonMap asMap();

    <T> JsonList<T> asList(Class<T> type);

	byte[] write(JsonMapper mapper);

}