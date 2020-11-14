package eu.eventstorm.sql.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class H2Functions {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(H2Functions.class);
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	private H2Functions() {
	}
	
	public static String json_value(String json, String key) throws IOException {
		Objects.requireNonNull(json);
		Objects.requireNonNull(key);

		LOGGER.info("json_value: json {}, key: {}", key, json);
		return String.valueOf(parseJson(json).get(key));
	}

	public static boolean json_exists(String json, String key) throws IOException {
		Objects.requireNonNull(json);
		Objects.requireNonNull(key);

		LOGGER.info("json_exists: json {}, key: {}", key, json);
		return parseJson(json).containsKey(key);
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> parseJson(String json) throws IOException {
		Objects.requireNonNull(json);
		return MAPPER.readValue(json, HashMap.class);
	}
	
}
