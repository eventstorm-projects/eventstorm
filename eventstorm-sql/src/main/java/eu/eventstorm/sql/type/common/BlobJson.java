package eu.eventstorm.sql.type.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.SqlTypeException;
/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BlobJson extends DefaultBlob implements Json {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Map<String, Object> map;

	private boolean isModified = false;

	public BlobJson(byte[] buf) {
		super(buf);
	}

	public BlobJson(Map<String, Object> value) throws IOException {
		super(MAPPER.writeValueAsBytes(value));
		this.map = value;
	}

	@Override
	public Set<String> keys() {
		checkInit();
		return ImmutableSet.copyOf(this.map.keySet());
	}

	@Override
	public void put(String key, Object value) {
		isModified = true;
		checkInit();
		this.map.put(key, value);
	}

	@Override
	public Object remove(String key) {
		isModified = true;
		checkInit();
		return this.map.remove(key);
	}

	@SuppressWarnings("unchecked")
	private void checkInit() {
		if (this.map == null) {
			if (this.length() == 0) {
				this.map = new HashMap<>();
				return;
			}
			try (InputStream is = super.getBinaryStream())  {
				this.map = MAPPER.readValue(is, Map.class);
			} catch (IOException cause) {
				throw new SqlTypeException(SqlTypeException.Type.READ_JSON, ImmutableMap.of("map", map), cause);
			}
		}

	}

	@Override
	public void flush() {
		if (isModified) {
			try {
				setBuf(MAPPER.writeValueAsBytes(this.map));
			} catch (IOException cause) {
				throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, ImmutableMap.of("map", map), cause);
			}
		}
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		checkInit();
		return clazz.cast(this.map.get(key));
	}
}