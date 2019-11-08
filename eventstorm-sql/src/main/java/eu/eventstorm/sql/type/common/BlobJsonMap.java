package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT_OBJECT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;
/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BlobJsonMap extends BlobJsonAdaptee implements JsonMap {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Map<String, Object> map;

    public BlobJsonMap(Map<String, Object> map) {
        setModified();
        this.map = map;
    }

    @SuppressWarnings("unchecked")
	public BlobJsonMap(byte[] content) {
        if (content == null || content.length == 0) {
            this.map = new HashMap<>();
        } else {
            try {
                this.map = MAPPER.readValue(content, Map.class);
            } catch (IOException cause) {
                throw new SqlTypeException(SqlTypeException.Type.READ_JSON, of(PARAM_CONTENT, content), cause);
            }
        }
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

	protected byte[] doWrite() {
        try {
            return MAPPER.writeValueAsBytes(this.map);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, map), cause);
        }
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		return clazz.cast(this.map.get(key));
	}

}