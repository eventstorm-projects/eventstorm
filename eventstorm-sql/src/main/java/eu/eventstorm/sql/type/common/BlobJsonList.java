package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT_OBJECT;

import java.io.IOException;
import java.util.List;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.SqlTypeException;
/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BlobJsonList extends BlobJsonAdaptee implements JsonList {

	private List<Object> list;

    public BlobJsonList(List<Object> list) {
        setModified();
        this.list = list;
    }

	protected byte[] doWrite(JsonMapper mapper) {
        try {
            return mapper.write(this.list);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, list), cause);
        }
	}

	@Override
	public <T> T get(int index, Class<T> clazz) {
	    return clazz.cast(this.list.get(index));
	}

	@Override
	public <T> void add(T value) {
        setModified();
        this.list.add(value);
	}

	@Override
	public <T> T remove(int index, Class<T> clazz) {
       return  clazz.cast(this.list.remove(index));
    }

	@Override
	public int size() {
		return this.list.size();
	}

}