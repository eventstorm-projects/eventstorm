package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT_OBJECT;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.json.JacksonJsonMapper;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DefaultJsonMap implements Json, JsonMap{

	private final Map<String, Object> map;
	private final JsonMapper mapper;
	
	public DefaultJsonMap() {
		this(new LinkedHashMap<>(), new JacksonJsonMapper());
	}
	
	public DefaultJsonMap(Map<String, ?> map) {
		this(map, new JacksonJsonMapper());
	}

	public DefaultJsonMap(Map<String, ?> map, JsonMapper jsonMapper) {
		this.map = new LinkedHashMap<>();
		this.map.putAll(map);
		this.mapper = jsonMapper;
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
		return this.map.remove(key);
	}

	@Override
	public JsonMap asMap() {	
		return this;
	}

	@Override
	public <T> JsonList<T> asList(Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] write() {
		try {
            return mapper.write(this.map);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, map), cause);
        }
	}

	@Override
	public String writeAsString() {
		try {
			return mapper.writeAsString(this.map);
		} catch (IOException cause) {
			throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, map), cause);
		}
	}

}
