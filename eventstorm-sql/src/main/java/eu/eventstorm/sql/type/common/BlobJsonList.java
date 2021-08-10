package eu.eventstorm.sql.type.common;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.type.SqlTypeException.PARAM_CONTENT_OBJECT;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.SqlTypeException;
/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BlobJsonList<T> extends BlobJsonAdaptee implements JsonList<T> {

	private final List<T> list;

    public BlobJsonList(List<T> list) {
        setModified();
        this.list = list;
    }

    @Override
	protected byte[] write(JsonMapper mapper) {
        try {
            return mapper.write(this.list);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, of(PARAM_CONTENT_OBJECT, list), cause);
        }
	}

	@Override
	public T get(int index) {
	    return this.list.get(index);
	}

	@Override
	public void add(T value) {
        setModified();
        this.list.add(value);
	}

	@Override
	public T remove(int index) {
       return  this.list.remove(index);
    }

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public List<T> copyOf() {
		return ImmutableList.copyOf(this.list);
	}
}