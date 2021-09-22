package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT_OBJECT;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;
/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class JsonAdapterMap extends JsonAdapter implements JsonMap {

	private final Map<String, Object> map;

    public JsonAdapterMap(Map<String, Object> map) {
        setModified();
        this.map = map;
    }

	@Override
	public Set<String> keys() {
		return ImmutableSet.copyOf(this.map.keySet());
	}

	@Override
	public void put(String key, Object value) {
		setModified();
		this.map.put(key, value);
	}

	@Override
	public Object remove(String key) {
		setModified();
		return this.map.remove(key);
	}

	@Override
	public byte[] write(JsonMapper mapper) {
        try {
            return mapper.write(this.map);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, map), cause);
        }
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		return clazz.cast(this.map.get(key));
	}

}