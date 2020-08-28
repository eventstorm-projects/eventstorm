package eu.eventstorm.sql.type.memory;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryJsonList implements Json, JsonList {

	private final List<Object> list;
	
	@SuppressWarnings("unchecked")
	public InMemoryJsonList(List<?> list) {
		this.list = (List<Object>) list;
	}

	@Override
	public JsonMap asMap() {
		throw new SqlTypeException(SqlTypeException.Type.AS_MAP_INVALID, ImmutableMap.of());
	}

	@Override
	public JsonList asList() {
		return this;
	}

	@Override
	public <T> T get(int index, Class<T> clazz) {
		return clazz.cast(this.list.get(index));
	}

	@Override
	public <T> void add(T value) {
		this.list.add(value);
	}

	@Override
	public <T> T remove(int index, Class<T> clazz) {
		return clazz.cast(this.list.remove(index));
	}

	@Override
	public int size() {
		return this.list.size();
	}
	
	@Override
	public byte[] write(JsonMapper mapper) {
		return "{}".getBytes();	}

}
