package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_ADAPTEE;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT;

import java.io.IOException;
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

    private BlobJsonAdaptee adaptee;
    private final JsonMapper mapper;

	public BlobJson(JsonMapper mapper, byte[] buf) {
        super(buf);
        adaptee = null;
        this.mapper = mapper;
    }

    public BlobJson(JsonMapper mapper,BlobJsonAdaptee adaptee) {
        super(DefaultBlob.EMTPY);
        this.adaptee = adaptee;
        this.mapper = mapper;
    }

	@Override
	public JsonMap asMap() {
        if (adaptee == null) {
        	if (getBuf() == null || getBuf().length == 0) {
        		this.adaptee = new BlobJsonMap(new HashMap<>());
            } else {
                try {
                	this.adaptee = new BlobJsonMap(mapper.readMap(getBuf()));
                } catch (IOException cause) {
                    throw new SqlTypeException(SqlTypeException.Type.READ_JSON, of(PARAM_CONTENT, getBuf()), cause);
                }
            }
            
        }
        if (adaptee instanceof JsonMap) {
            return JsonMap.class.cast(adaptee);
        }
		throw new SqlTypeException(SqlTypeException.Type.AS_MAP_INVALID, of(PARAM_ADAPTEE, adaptee));
	}

	@Override
	public JsonList asList() {
		if (adaptee == null) {
			if (getBuf() == null || getBuf().length == 0) {
        		this.adaptee = new BlobJsonMap(new HashMap<>());
            } else {
                try {
                	this.adaptee = new BlobJsonList(mapper.readList(getBuf()));
                } catch (IOException cause) {
                    throw new SqlTypeException(SqlTypeException.Type.READ_JSON, of(PARAM_CONTENT, getBuf()), cause);
                }
            }
        }
        if (adaptee instanceof JsonList) {
            return JsonList.class.cast(adaptee);
        }
		throw new SqlTypeException(SqlTypeException.Type.AS_LIST_INVALID, of(PARAM_ADAPTEE, adaptee));
	}

    @Override
	public void flush() {
        if (adaptee != null) {
            byte[] content = this.adaptee.write(this.mapper);
            if (content != null) {
                setBuf(content);
            }
        }
	}
}