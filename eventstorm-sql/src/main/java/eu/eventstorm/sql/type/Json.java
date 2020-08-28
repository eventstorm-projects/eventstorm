package eu.eventstorm.sql.type;

import eu.eventstorm.sql.JsonMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Json {

    JsonMap asMap();

    JsonList asList();

	byte[] write(JsonMapper mapper);

}