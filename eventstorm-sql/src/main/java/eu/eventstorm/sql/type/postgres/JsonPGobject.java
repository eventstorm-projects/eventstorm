package eu.eventstorm.sql.type.postgres;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;
import eu.eventstorm.sql.type.common.JsonAdapter;
import eu.eventstorm.sql.type.common.JsonAdapterList;
import eu.eventstorm.sql.type.common.JsonAdapterMap;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_ADAPTEE;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT;

public final class JsonPGobject extends PGobject implements Json {

    private final JsonMapper mapper;
    private JsonAdapter adapter;

    public JsonPGobject(String value, JsonMapper mapper) throws SQLException {
        setType("json");
        setValue(value);
        this.mapper = mapper;
    }

    @Override
    public JsonMap asMap() {
        if (adapter == null) {
            if (this.value == null) {
                this.adapter = new JsonAdapterMap(new HashMap<>());
            } else {
                try {
                    this.adapter = new JsonAdapterMap(mapper.readMap(this.value.getBytes(StandardCharsets.UTF_8)));
                } catch (IOException cause) {
                    throw new SqlTypeException(SqlTypeException.Type.READ_JSON, of(PARAM_CONTENT, this.value), cause);
                }
            }
        }
        if (adapter instanceof JsonMap) {
            return (JsonMap) adapter;
        }
        throw new SqlTypeException(SqlTypeException.Type.AS_MAP_INVALID, of(PARAM_ADAPTEE, adapter));
    }

    @Override
    public <T> JsonList<T> asList(Class<T> type) {
        if (adapter == null) {
            if (this.value == null) {
                this.adapter = new JsonAdapterList<T>(new ArrayList<>());
            } else {
                try {
                    this.adapter = new JsonAdapterList<>(mapper.readList(this.value.getBytes(StandardCharsets.UTF_8), type));
                } catch (IOException cause) {
                    throw new SqlTypeException(SqlTypeException.Type.READ_JSON, of(PARAM_CONTENT, this.value), cause);
                }
            }
        }
        if (adapter instanceof JsonList) {
            return (JsonList<T>) adapter;
        }
        throw new SqlTypeException(SqlTypeException.Type.AS_LIST_INVALID, of(PARAM_ADAPTEE, adapter));
    }

    @Override
    public byte[] write(JsonMapper mapper) {
        String value = getValue();
        if (value == null) {
            return null;
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String writeAsString(JsonMapper mapper) {
        return getValue();
    }

    @Override
    public String getValue() {
        if (adapter != null && adapter.isModified()) {
            this.value = new String(adapter.write(mapper), StandardCharsets.UTF_8);
        }
        return super.getValue();
    }
}
