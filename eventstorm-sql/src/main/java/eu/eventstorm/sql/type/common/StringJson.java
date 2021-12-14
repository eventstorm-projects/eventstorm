package eu.eventstorm.sql.type.common;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;
import eu.eventstorm.util.Strings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_ADAPTEE;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class StringJson implements Json {

    private String content;
    private JsonAdapter adapter;
    private final JsonMapper mapper;


	public StringJson(JsonMapper mapper, String content) {
        this.adapter = null;
        this.mapper = mapper;
        this.content = content;
    }

    public StringJson(JsonMapper mapper, JsonAdapter adaptee) {
        this.adapter = adaptee;
        this.mapper = mapper;
        this.content = null;
    }

	@Override
	public JsonMap asMap() {
        if (adapter == null) {
            if (Strings.isEmpty(content)) {
        		this.adapter = new JsonAdapterMap(new HashMap<>());
            } else {
                try {
                	this.adapter = new JsonAdapterMap(mapper.readMap(content));
                } catch (IOException cause) {
                    throw new SqlTypeException(SqlTypeException.Type.READ_JSON, of(PARAM_CONTENT, content), cause);
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
            if (Strings.isEmpty(content)) {
        		this.adapter = new JsonAdapterList(new ArrayList<>());
            } else {
                try {
                	this.adapter = new JsonAdapterList(mapper.readList(content, type));
                } catch (IOException cause) {
                    throw new SqlTypeException(SqlTypeException.Type.READ_JSON, of(PARAM_CONTENT, content), cause);
                }
            }
        }
        if (adapter instanceof JsonList) {
            return (JsonList) adapter;
        }
		throw new SqlTypeException(SqlTypeException.Type.AS_LIST_INVALID, of(PARAM_ADAPTEE, adapter));
	}

	@Override
	public byte[] write(JsonMapper mapper) {
		if (adapter != null && this.adapter.isModified()) {
			content = this.adapter.writeAsString(mapper);
		}
        if (content != null) {
            return content.getBytes(StandardCharsets.UTF_8);
        } else {
            return null;
        }
	}

    @Override
    public String writeAsString(JsonMapper mapper) {
        if (adapter != null && this.adapter.isModified()) {
            content = this.adapter.writeAsString(mapper);
        }
        return content;
    }

}