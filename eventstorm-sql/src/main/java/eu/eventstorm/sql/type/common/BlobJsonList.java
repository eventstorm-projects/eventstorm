package eu.eventstorm.sql.type.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.type.SqlTypeException;
/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BlobJsonList extends BlobJsonAdaptee implements JsonList {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private List<Object> list;

    public BlobJsonList(List<Object> list) {
        this.list = list;
    }

    @SuppressWarnings("unchecked")
	public BlobJsonList(byte[] content) {
        if (content == null || content.length == 0) {
            this.list = new ArrayList<>();
        } else {
            try {
                this.list = MAPPER.readValue(content, List.class);
            } catch (IOException cause) {
                throw new SqlTypeException(SqlTypeException.Type.READ_JSON, ImmutableMap.of("content", new String(content, StandardCharsets.UTF_8)), cause);
            }
        }
	}

	protected byte[] doWrite() {
        try {
            return MAPPER.writeValueAsBytes(this.list);
        } catch (IOException cause) {
            throw new SqlTypeException(SqlTypeException.Type.WRITE_JSON, ImmutableMap.of("list", list), cause);
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