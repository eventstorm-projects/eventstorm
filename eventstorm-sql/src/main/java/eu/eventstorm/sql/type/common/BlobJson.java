package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_ADAPTEE;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.JsonMap;
import eu.eventstorm.sql.type.SqlTypeException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BlobJson extends DefaultBlob implements Json {

    private JsonAdapter adapter;
    private final JsonMapper mapper;

	public BlobJson(JsonMapper mapper, byte[] buf) {
        super(buf);
        adapter = null;
        this.mapper = mapper;
    }

    public BlobJson(JsonMapper mapper, JsonAdapter adaptee) {
        super(DefaultBlob.EMPTY);
        this.adapter = adaptee;
        this.mapper = mapper;
    }

	@Override
	public JsonMap asMap() {
        if (adapter == null) {
        	if (getBuf() == null || getBuf().length == 0) {
        		this.adapter = new JsonAdapterMap(new HashMap<>());
            } else {
                try {
                	this.adapter = new JsonAdapterMap(mapper.readMap(getBuf()));
                } catch (IOException cause) {
                    throw new SqlTypeException(SqlTypeException.Type.READ_JSON, of(PARAM_CONTENT, getBuf()), cause);
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
			if (getBuf() == null || getBuf().length == 0) {
        		this.adapter = new JsonAdapterList(new ArrayList<>());
            } else {
                try {
                	this.adapter = new JsonAdapterList(mapper.readList(getBuf(), type));
                } catch (IOException cause) {
                    throw new SqlTypeException(SqlTypeException.Type.READ_JSON, of(PARAM_CONTENT, getBuf()), cause);
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
			setBuf(this.adapter.write(mapper));
		}
		return getBuf();
	}

    @Override
    public String writeAsString(JsonMapper mapper) {
        if (adapter != null && this.adapter.isModified()) {
            setBuf(this.adapter.write(mapper));
        }
        return new String(getBuf(), StandardCharsets.UTF_8);
    }

}