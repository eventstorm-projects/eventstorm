package eu.eventstorm.sql.type.common;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.json.JacksonJsonMapper;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;
import eu.eventstorm.util.ToStringBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT_OBJECT;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DefaultJsonList<T> implements Json, JsonList<T> {

    private final List<T> list;
    private final JsonMapper mapper;

    public DefaultJsonList() {
        this(new ArrayList<>());
    }

    public DefaultJsonList(List<T> list) {
        this(list, new JacksonJsonMapper());
    }

    public DefaultJsonList(List<T> list, JsonMapper mapper) {
        this.list = new ArrayList<>(list.size());
        this.list.addAll(list);
        this.mapper = mapper;
    }

    @Override
    public T get(int index) {
        return this.list.get(index);
    }

    @Override
    public void add(T value) {
        this.list.add(value);
    }

    @Override
    public T remove(int index) {
        return this.list.remove(index);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public List<T> copyOf() {
        return ImmutableList.copyOf(this.list);
    }

    @Override
    public Iterable<T> iterable() {
        return this.list;
    }

    @Override
    public JsonMap asMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> JsonList<T> asList(Class<T> type) {
        return (JsonList<T>) this;
    }

    @Override
    public byte[] write() {
        try {
            return mapper.write(this.list);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, list), cause);
        }
    }

    @Override
    public String writeAsString() {
        try {
            return mapper.writeAsString(this.list);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, list), cause);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, false)
                .append("list", this.list)
                .toString();
    }

}
