package eu.eventstorm.sql.json;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.sql.JsonMapper;

public final class JacksonJsonMapper implements JsonMapper{

	private final ObjectMapper objectMapper;
	
	public JacksonJsonMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public JacksonJsonMapper() {
		this(new ObjectMapper());
	}

	@Override
	public Map<String, Object> readMap(byte[] content) throws IOException {
		return this.objectMapper.readValue(content, Map.class);
	}

	@Override
	public <T> List<T> readList(byte[] content, Class<T> type) throws IOException {
		return this.objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
	}
	
	@Override
	public byte[] write(Map<String, Object> map) throws IOException {
		return this.objectMapper.writeValueAsBytes(map);
	}

	@Override
	public byte[] write(List<?> list) throws IOException {
		return this.objectMapper.writeValueAsBytes(list);
	}

}
