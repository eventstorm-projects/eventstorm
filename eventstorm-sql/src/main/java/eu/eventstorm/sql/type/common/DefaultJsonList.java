package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT_OBJECT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DefaultJsonList implements Json, JsonList{

	private final List<Object> list;
	
	public DefaultJsonList() {
		this.list = new ArrayList<>();
	}
	
	public DefaultJsonList(List<?> list) {
		this.list = new ArrayList<>(list.size());
		this.list.addAll(list);
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
	public <T> List<T> copyOf() {
		return (List<T>) ImmutableList.copyOf(this.list);
	}

	@Override
	public JsonMap asMap() {	
		throw new UnsupportedOperationException();
	}

	@Override
	public JsonList asList() {
		return this;
	}

	@Override
	public byte[] write(JsonMapper mapper) {
		try {
            return mapper.write(this.list);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, list), cause);
        }
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, false)
				.append("list" , this.list)
				.toString();
	}

}
