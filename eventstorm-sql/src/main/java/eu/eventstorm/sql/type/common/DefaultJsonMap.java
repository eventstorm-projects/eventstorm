package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT_OBJECT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DefaultJsonMap implements Json, JsonMap{

	private final Map<String, Object> map;
	
	public DefaultJsonMap() {
		this.map = new HashMap<>();
	}
	
	public DefaultJsonMap(Map<String, ?> map) {
		this.map = new HashMap<>();
		map.forEach((key, value) -> {
			this.map.put(key, value);
		});
	}
	
	@Override
	public Set<String> keys() {
		return this.map.keySet();
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		return clazz.cast(this.map.get(key));
	}

	@Override
	public void put(String key, Object value) {
		this.map.put(key, value);
	}

	@Override
	public Object remove(String key) {
		return this.remove(key);
	}

	@Override
	public JsonMap asMap() {	
		return this;
	}

	@Override
	public JsonList asList() {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] write(JsonMapper mapper) {
		try {
            return mapper.write(this.map);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, map), cause);
        }
	}

}
